import os
import sys
import cv2
import logging
import mysql.connector
from deepface import DeepFace
import tensorflow as tf

# 🔥 Suppress TensorFlow logs
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "3"
os.environ["TF_ENABLE_ONEDNN_OPTS"] = "0"

sys.stdout = open(os.devnull, "w")
sys.stderr = open(os.devnull, "w")

logging.getLogger("tensorflow").setLevel(logging.FATAL)
logging.getLogger("deepface").setLevel(logging.FATAL)

# ✅ Now import TensorFlow & DeepFace
import tensorflow as tf
from deepface import DeepFace

# 🔄 Restore standard output after imports
sys.stdout = sys.__stdout__
sys.stderr = sys.__stderr__


# Ensure an image path is provided
if len(sys.argv) < 2:
    print("No image provided.")
    sys.exit(1)

image_path = sys.argv[1]

# Verify if the input image exists
if not os.path.exists(image_path):
    print("Error: Image not found")
    sys.exit(1)

# Connect to the database
try:
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="db_evencia"
    )
    cursor = conn.cursor()

    # Fetch stored user profile images
    cursor.execute("SELECT email, profile_image FROM user")
    users = cursor.fetchall()

except mysql.connector.Error:
    print("Database connection error")
    sys.exit(1)

# Face Recognition Process
try:
    recognized_user = None

    for user_email, profile_image in users:
        # Skip users with no profile image
        if not profile_image or profile_image.strip() == "":
            continue  

        # Ensure the profile image exists
        if not os.path.exists(profile_image):
            continue  

        # Perform face recognition
        verification = DeepFace.verify(image_path, profile_image, model_name="Facenet", enforce_detection=False)

        if verification.get("verified"):
            recognized_user = user_email
            break  # Stop at first match

    # Print ONLY the recognized email for Java to process
    if recognized_user:
        print(recognized_user)  
    else:
        print("Face not recognized.")

except Exception:
    print("Error: Face recognition failed.")

# Close database connection
cursor.close()
conn.close()
