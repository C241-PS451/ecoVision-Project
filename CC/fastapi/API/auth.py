from fastapi import APIRouter, HTTPException, Body, UploadFile, File, Form
from pydantic import BaseModel, EmailStr
from firebase_admin import auth
import bcrypt
from datetime import datetime
from firebase_admin import exceptions as firebase_exceptions
from .init_firebase import db, bucket

# Initialize FastAPI router
router = APIRouter()

# Pydantic models for request bodies
class User(BaseModel):
    username: str
    email: EmailStr
    password: str

class LoginModel(BaseModel):
    email: EmailStr
    password: str

class UpdateUserModel(BaseModel):
    full_name: str = None
    birthday: datetime = None
    location: str = None

# Helper functions for password hashing and verification
def hash_password(password):
    hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
    return hashed_password.decode('utf-8')

def verify_password(plain_password, hashed_password):
    return bcrypt.checkpw(plain_password.encode('utf-8'), hashed_password.encode('utf-8'))

# Endpoint for user registration
@router.post("/register/")
async def register(user: User = Body(...)):
    try:
        users_ref = db.collection('users')
        
        # Check if username is already registered
        query_username = users_ref.where('username', '==', user.username).limit(1).stream()
        if any(query_username):
            raise HTTPException(status_code=400, detail="Username already registered")

        # Check if email is already registered
        query_email = users_ref.where('email', '==', user.email).limit(1).stream()
        if any(query_email):
            raise HTTPException(status_code=400, detail="Email already registered")

        # Hash password before storing in Firestore
        hashed_password = hash_password(user.password)

        # Save user data to Firestore
        user_data = {
            'username': user.username,
            'email': user.email,
            'password': hashed_password
        }
        users_ref.add(user_data)

        return {"message": "User registered successfully"}

    except firebase_exceptions.FirebaseError as e:
        print(f"Firebase Error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Firebase Error: {str(e)}")
    except Exception as e:
        print(f"Internal Server Error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Internal Server Error: {str(e)}")

# Endpoint for user login
@router.post("/login/")
async def login(login: LoginModel = Body(...)):
    try:
        users_ref = db.collection('users')

        # Query for email
        query_email = users_ref.where('email', '==', login.email).limit(1).stream()
        user_docs = list(query_email)
        if not user_docs:
            raise HTTPException(status_code=400, detail="Invalid email or password")

        user_doc = user_docs[0]
        user_data = user_doc.to_dict()

        # Verify password
        if not verify_password(login.password, user_data['password']):
            raise HTTPException(status_code=400, detail="Invalid email or password")

        # Generate Firebase custom token
        custom_token = auth.create_custom_token(user_data['email'])
        
        response_data = {
            "message": "Login successful",
            "username": user_data['username'],
            "access_token": custom_token.decode('utf-8'),
            "token_type": "bearer",
            "document_id": user_doc.id  # Adding document ID to response
        }
        return response_data

    except firebase_exceptions.FirebaseError as e:
        print(f"Firebase Error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Firebase Error: {str(e)}")
    except Exception as e:
        print(f"Internal Server Error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Internal Server Error: {str(e)}")

# Endpoint to update user information including photo
@router.put("/update_user/{user_id}")
async def update_user(user_id: str,
                      full_name: str = Form(None),
                      birthday: datetime = Form(None),
                      location: str = Form(None),
                      photo: UploadFile = File(None)):
    try:
        user_ref = db.collection('users').document(user_id)
        user_doc = user_ref.get()

        if not user_doc.exists:
            raise HTTPException(status_code=404, detail="User not found")

        # Convert date to datetime if it's provided
        if birthday:
            birthday_datetime = datetime(birthday.year, birthday.month, birthday.day)
        else:
            birthday_datetime = None

        # Collect update data
        update_data_dict = {}
        if full_name:
            update_data_dict["full_name"] = full_name
        if birthday_datetime:
            update_data_dict["birthday"] = birthday_datetime
        if location:
            update_data_dict["location"] = location

        # Handle photo upload
        if photo:
            # Upload photo to Google Cloud Storage
            blob = bucket.blob(f'profile/{user_id}_{photo.filename}')
            blob.upload_from_file(photo.file, content_type=photo.content_type)
            blob.make_public()
            update_data_dict["photo_url"] = blob.public_url

        # Update user document with new data
        user_ref.update(update_data_dict)

        return {"message": "User information updated successfully"}

    except firebase_exceptions.FirebaseError as e:
        print(f"Firebase Error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Firebase Error: {str(e)}")
    except Exception as e:
        print(f"Internal Server Error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Internal Server Error: {str(e)}")