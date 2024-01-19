import logging
import os
import secrets

from flask import Flask, g, jsonify, request
from flask_wtf.csrf import CSRFProtect
from marshmallow import Schema, ValidationError, fields
from transformers import BlenderbotForConditionalGeneration, BlenderbotTokenizer


# Scheme definition using Marshmallow
class QuestionSchema(Schema):
    question = fields.Str(required=True, validate=lambda x: 0 < len(x) < 200)


# Scheme instance
question_schema = QuestionSchema()

app = Flask(__name__)
app.secret_key = secrets.token_hex(16)

# Flask-WTF configuration
app.config["WTF_CSRF_CHECK_DEFAULT"] = False
app.config["WTF_CSRF_TIME_LIMIT"] = None

csrf = CSRFProtect(app)


# Function to load or generate a CSRF token
def load_or_create_csrf_token(file_path):
    if os.path.exists(file_path):
        with open(file_path, "r") as file:
            token = file.read()
            if token:
                return token
    # If the file does not exist or is empty, generate a new token
    token = secrets.token_hex(32)
    with open(file_path, "w") as file:
        file.write(token)
    return token


# Load a CSRF token from a file or create a new one
csrf_token_file_path = "csrf_token.txt"
server_csrf_token = load_or_create_csrf_token(csrf_token_file_path)


@app.before_request
def before_request():
    g.csrf_token = server_csrf_token


# Define model name and path
model_name = "facebook/blenderbot-400M-distill"
model_dir = "./blenderbot-400M-distill"  # Change to the appropriate path

# Set up logging
logging.basicConfig(level=logging.DEBUG)

logging.debug(server_csrf_token)


# Function to check for the existence of the model and download it if necessary
def load_or_download_model(m_name, m_dir):
    if not os.path.exists(m_dir):
        os.makedirs(m_dir)
        logging.debug(f"Downloading model {m_name} to {m_dir}...")
        m = BlenderbotForConditionalGeneration.from_pretrained(m_name)
        t = BlenderbotTokenizer.from_pretrained(m_name)
        m.save_pretrained(m_dir)
        t.save_pretrained(m_dir)
        logging.debug("Model downloaded and saved.")
    else:
        logging.debug(f"Loading model from {m_dir}...")
        m = BlenderbotForConditionalGeneration.from_pretrained(m_dir)
        t = BlenderbotTokenizer.from_pretrained(m_dir)
    return m, t


# Load or download the model at application startup
model, tokenizer = load_or_download_model(model_name, model_dir)


@app.route("/ask", methods=["POST"])
def ask():
    # Get the CSRF token from the global context
    csrf_token = g.get("csrf_token", None)

    # Get the CSRF token from the request headers
    request_csrf_token = request.headers.get("X-CSRFToken", None)

    # Check that the CSRF tokens match
    if not request_csrf_token or request_csrf_token != csrf_token:
        return jsonify({"error": "CSRF token does not match"}), 400

    try:
        # Get the question from the request
        data = question_schema.load(request.json)
        question = data["question"]

        # Process the question through the model
        inputs = tokenizer(question, return_tensors="pt")
        reply_ids = model.generate(**inputs)
        answer = tokenizer.decode(reply_ids[0], skip_special_tokens=True)

        # Return the answer
        return jsonify({"answer": answer})
    except ValidationError as err:
        return jsonify(err.messages), 400
    except Exception as e:
        logging.error(str(e))
        return jsonify({"error": str(e)}), 500


@app.route("/ask/state", methods=["GET"])
def check_state():
    return jsonify({"status": "OK"}), 200


if __name__ == "__main__":
    app.run(debug=True)
