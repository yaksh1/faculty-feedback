# ReactJS-Spring-WebApp

This project consists of two applications: a Spring Boot backend API (`spring-backend`) and a ReactJS frontend (`react-frontend`). The platform focuses on providing features for users to interact with faculty data, submit reviews, and manage their profiles, while leveraging secure authentication and authorization mechanisms.

## Overview

- **Backend (Spring Boot)**: Provides secure endpoints for user authentication (login/signup), email verification (OTP), password management, review submission, and data storage.
  - **JWT Authentication**: Secures endpoints with JWT tokens, ensuring only authorized users can access restricted features.
  - **Perspective API Integration**: Analyzes user reviews for toxicity and sentiment before storing them.
  - **Bcrypt**: Passwords are hashed using Bcrypt before storing them in the database.
  - **Database**: User data is stored in MySQL, while faculty and review data are stored in MongoDB.

- **Frontend (ReactJS)**: The frontend provides a user interface for searching faculty members, submitting reviews, and managing user accounts after login.
  - Users can log in with a verified email and navigate through the faculty directory.
  - Reviews are submitted for each faculty member and are analyzed for toxicity before being stored.

### Prerequisites

- **Java 11+** (for the backend)
- **ReactJS** (for the frontend)
- **MySQL** (for user data)
- **MongoDB** (for faculty and review data)
- **JWT** (for authentication)

## Features

- **User Authentication**: Login/signup with email verification using OTP.
- **JWT Token**: Used for securing API endpoints, ensuring only authorized users can access them.
- **Email Verification**: Sends OTP for user verification. Resend verification code available.
- **Change Password**: Users can change their passwords after logging in.
- **Custom Exception Handling**: Handles common errors gracefully and provides meaningful feedback.
- **Restricted Access**: Initially, only students with my college email ID are allowed to use the platform (currently disabled).
- **Account Banning**: Users submitting inappropriate reviews are banned for a period, with each consecutive offense doubling the ban duration.
- **Toxicity and Sentiment Analysis**: Reviews are analyzed using the Perspective API for sentiment and toxicity, with inappropriate reviews triggering bans.
- **Database**:
  - **MySQL** for user data storage.
  - **MongoDB** for storing faculty information and user reviews.
- **Bcrypt**: Passwords are securely hashed before storing in the database.

## Applications

### spring-backend

The backend is a Spring Boot application that exposes a REST API to manage user authentication, faculty, and reviews. It is secured with JWT tokens, which are required to access restricted endpoints. Data is stored in MySQL for user information and MongoDB for faculty and review data.

#### API Endpoints
base url -> `http://localhost:8080`
- `/auth/register` - Register a new user
- `/auth/login` - Get a JWT token after successful login
- `/auth/verify` - Email verification with OTP
- `/auth/forgotPassword` - Change the user's password
- `/auth/resend` - Resend email verification code
- `/user/faculties/{facultyID}` - Search and retrieve faculty data
- `/user/faculties` - shows all faculties from DB
- `/user/reviews` - Submit and manage reviews for faculty (also manages banning logic)

### react-frontend

The frontend is a ReactJS application that allows users to search for faculty, submit reviews, and manage their profiles after logging in with a verified email.

- **Faculty Search**: Users can search for faculty members.
- **Review Submission**: Users can submit reviews on faculty members, which are analyzed for toxicity before being stored.
- **Login**: Users log in with their verified email and access the platform’s features.

### Setup

1. **Setup MongoDb Atlas and MongoDB Compass**
- Watch [this video](https://youtu.be/5PdEmeopJVQ?si=X5ogICwjISWqA4uX&t=373) to get a better explanation.
- Keep the collection names as "faculties" for faculty data and "reviews" for review data in mongoDB compass databse.
- Import documents in `faculties` from provided json in repository.

2. **Setup MySQL Database**
- Create a MySQL database locally with your choice of name. For example I have created `user_management`.

3. **Enabe Google Perspective API** (*Skip this step if you dont want toxicity/negative review anayzer*)
- Request the usage of the API using this [link](https://docs.google.com/forms/d/e/1FAIpQLSdhBBnVVVbXSElby-jhNnEj-Zwpt5toQSCFsJerGfpXW66CuQ/viewform). After getting confirmation from google, generate your API_KEY to starting sending requests.

4. **Code Setup**

Clone the repository

```bash
git clone https://github.com/yaksh1/faculty-feedback.git
```

Navigate to newly cretaed Folder

```bash
cd faculty-feedback
```

### FRONTEND

navigate to react-frontend subfolder
```bash
cd react-frontend
```

install modules
```bash
npm install
```

Start application
```bash
npm start
```
Navigate to http://localhost:3000

### BACKEND

navigate to spring-backend subfolder
```bash
cd spring-backend
```

  - Create a .env file
    - Fill the details of the project in the `sample-env.md` and paste it in your `.env` file.
    - For email app password watch this [video](https://www.youtube.com/watch?v=lSURGX0JHbA).
    - For JWT secret key use any from google or other source
    - Get Perspective API Key from google cloud console
    - MongoDB details are provided after you setup mongo atlas and compass
       - example:

         spring.data.mongodb.uri =  mongodb+srv://foobar:XXXXXXXXXXXXXX@foo-bar-cluster.1mybc.mongodb.net
         ```bash
           MONGO_CLUSTER=foo-bar-cluster.1mybc.mongodb.net
           MONGO_PASSWORD=XXXXXXXXXXXXXX
           MONGO_USER=foobar
           MONGO_DATABASE=foobar-database (from mongodb compass)
	        ```
  - Run the Springboot Application
    
```bash
./mvnw spring-boot:run
```

Backend runs on http://localhost:8080

### Mail Configuration

Make sure to specify valid `spring.mail.username` and `spring.mail.password` in the `application.properties` file to enable email services for sending verification emails. Without proper mail credentials, email functionality will not work.

