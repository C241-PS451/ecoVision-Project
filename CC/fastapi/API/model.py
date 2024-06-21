from fastapi import APIRouter, UploadFile, File, HTTPException
import numpy as np
import tensorflow as tf
from PIL import Image, UnidentifiedImageError
import io
import logging
from firebase_admin import storage
from .init_firebase import db, bucket
import uuid
from datetime import datetime

router = APIRouter()

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Load the TensorFlow Lite model
try:
    interpreter = tf.lite.Interpreter(model_path="ecovision.tflite")
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    logger.info("TensorFlow Lite model loaded successfully")
except Exception as e:
    logger.error(f"Failed to load TensorFlow Lite model: {e}")
    raise HTTPException(status_code=500, detail="Failed to load TensorFlow Lite model")

class_map = {
    0: "HDPE",
    1: "PET",
    2: "PVC",
    3: "LDPE",
    4: "PP",
    5: "PS",
    6: "Other"
}

# Function to process the image
def process_image(contents):
    try:
        image = Image.open(io.BytesIO(contents))
        image = image.convert("RGB")
        image = image.resize((200, 200))  # Resize image
        image = np.array(image) / 255.0  # Normalize 
        image = np.expand_dims(image, axis=0)  # Add batch dimension
        return image
    except UnidentifiedImageError:
        logger.error("Invalid image file")
        raise HTTPException(status_code=400, detail="Invalid image file")

# Function to upload image to Firebase Storage
def upload_img(image_contents, metadata):
    try:
        bucket = storage.bucket()
        blob = bucket.blob(f"images/{uuid.uuid4()}.jpg")
        blob.metadata = {"metadata": metadata}
        blob.upload_from_string(image_contents, content_type="image/jpeg")
        blob.make_public()
        logger.info("Image uploaded to Firebase Storage successfully")
        return blob.public_url
    except Exception as e:
        logger.error(f"Failed to upload image to Firebase Storage: {e}")
        raise HTTPException(status_code=500, detail="Failed to upload image to Firebase Storage")

@router.post("/predict/")
async def predict(file: UploadFile = File(...)):
    try:
        contents = await file.read()
        image = process_image(contents)
        
        # Feed the image data into the TensorFlow Lite model
        interpreter.set_tensor(input_details[0]['index'], image.astype(np.float32))
        interpreter.invoke()

        # Get the output from the model
        output_data = interpreter.get_tensor(output_details[0]['index'])
        logger.info(f"Model raw output: {output_data}")
        predicted_class = np.argmax(output_data, axis=1)[0]

        logger.info(f"Predicted class: {predicted_class}")

        # Prepare metadata with the prediction result and current timestamp
        metadata = {
            "predicted_class": class_map.get(predicted_class, "Unknown"),
            "prediction_time": datetime.now().isoformat()
        }

        # Upload the original image to Firebase Storage with metadata
        image_url = upload_img(contents, metadata)

        return {
            "predicted_class": class_map.get(predicted_class, "Unknown"),
            "image_url": image_url,
            "prediction_time": metadata["prediction_time"]
        }
    except Exception as e:
        logger.error(f"Error during prediction: {e}")
        raise HTTPException(status_code=500, detail="Internal Server Error")
