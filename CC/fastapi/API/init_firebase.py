from firebase_admin import credentials, initialize_app, firestore, storage

# Path to your Firebase service account key
cred = credentials.Certificate("serviceaccountkey.json")
app = initialize_app(cred, {'storageBucket': 'flash-parity-424914-r9.appspot.com'})

# Initialize Firestore client
db = firestore.client()

# Initialize Google Cloud Storage client
bucket = storage.bucket()
