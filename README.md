# Repy - Android Application

![image](https://github.com/user-attachments/assets/1543d1e9-1eb3-440e-9c48-5eccfdf9b75d)

Repy is an Android application that allows users to generate and manage appeals based on issued tickets. The app offers functionalities such as signing up, logging in, generating appeals, monitoring appeals, and managing user profiles.

## Features

- **User Authentication**: Users can sign up and log in using their email and password. Firebase Authentication is used to handle user authentication.
- **Ticket Management**: Users can create tickets by providing necessary details such as date of issue, ticket ID, car number, cause, and amount. The app supports two types of tickets: municipality and police.
- **Appeal Generation**: Users can generate appeals based on tickets. The application leverages the OpenAI API to generate a formal appeal document, which is then saved as a PDF in Firebase Storage.
- **Profile Management**: Users can view and update their profile, including uploading a profile picture. The profile information is stored and retrieved from Firebase Realtime Database.
- **Appeal Monitoring and History**: Users can monitor the status of their active appeals and view the history of previous appeals.
- **Image Loading with Glide**: The application uses Glide, a fast and efficient open-source media management and image loading framework for Android, which wraps media decoding, memory and disk caching, and resource pooling into a simple and easy-to-use interface. Glide is used for loading and displaying user profile images.

## Technology Stack

- **Android SDK**: The primary framework for building the application.
- **Firebase**: Firebase Authentication for user authentication, Firebase Realtime Database for data storage, and Firebase Storage for storing user profile images and appeal PDFs.
- **OpenAI GPT**: Used for generating appeal documents.
- **Retrofit**: Used for making API calls to external services, including fetching country and city data.
- **iTextPDF**: Used for generating PDF documents within the application.
- **Glide**: Used for efficient image loading and caching in the application.

## APIs and Libraries Used

### APIs

- **ChatGPT API**: Used to generate appeal documents based on user input and ticket details. This API is provided by OpenAI and integrated into the application using the Retrofit library.
  
- **Countries & Cities API**: Provides country and city information, including country flags and currencies. This API is used for setting up country and city spinners in the application. More details about this API can be found [here](https://documenter.getpostman.com/view/1134062/T1LJjU52).

### GitHub Resources

- **`de.hdodenhof:circleimageview:3.1.0`**: A popular library for creating circular ImageView widgets in Android. This library is used in the application for displaying user profile images in a circular frame.

- **`io.github.tashilapathum:please-wait:0.5.0`**: A lightweight library for displaying customizable progress dialogs in Android. This library is used in the application to show progress dialogs while generating appeal documents.


## Installation and Setup

1. **Clone the repository**:
    ```bash
    git clone https://github.com/RoyRubinIL/Repy.git
    ```

2. **Open the project in Android Studio**:
   - Ensure you have the latest version of Android Studio installed.
   - Open the project directory in Android Studio.

3. **Add your Firebase Configuration**:
   - Place your `google-services.json` file in the `app` directory.

4. **Add your OpenAI API Key**:
   - Replace the placeholder in `ChatGPTService` with your OpenAI API key.

5. **Build and Run**:
   - Connect an Android device or start an emulator.
   - Build and run the project from Android Studio.
  
## Video Demo
[VideoDemo](https://drive.google.com/file/d/13EIp6wvhriUstBs9tqd6bjwwr-deTYz5/view?usp=sharing)

## Credits

- **Images**: Generated using [ChatGPT DALL-E](https://openai.com/dall-e-2).
- **Icons**: Provided by [Flaticon](https://www.flaticon.com/icon-fonts-most-downloaded).

## Acknowledgments

- [OpenAI](https://www.openai.com/) for providing the GPT API used for generating appeal documents.
- [Countries & Cities API](https://documenter.getpostman.com/view/1134062/T1LJjU52) for providing country and city information.
- [Glide](https://github.com/bumptech/glide) for efficient image loading and caching.
- [Firebase](https://firebase.google.com/) for authentication, storage, and database services.
- [Retrofit](https://square.github.io/retrofit/) for seamless API integration.
- [iTextPDF](https://itextpdf.com/) for generating PDFs.
- [CircleImageView](https://github.com/hdodenhof/CircleImageView) by hdodenhof for the circular image view.
- [PleaseWait](https://github.com/tashilapathum/PleaseWait) by tashilapathum for the customizable progress dialog.




