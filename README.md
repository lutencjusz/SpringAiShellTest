
# SpringDocumentationAi

SpringDocumentationAi is a Spring Boot-based project that can read EPUB or PDF documents, vectorization by vectors database and perform question to ChatGPT. Application additionally uses OAuth2 authorization by Google or GitHub integration. 

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [OAuth2 GitHub Configuration](#oauth2-github-configuration)
- [Running the Project](#running-the-project)
- [Technologies Used](#technologies-used)
- [Contributing](#contributing)

## Requirements

Before starting, ensure that you have the following installed:

- JDK 11 or newer
- Maven 3.6.3 or newer
- PostgreSQL 13 or newer
- Git
- OpenAI API Key
- Mail API Secret
- admin email

Additional requirements for OAuth2:
- Google OAuth2 Client ID and Client Secret
- GitHub OAuth2 Client ID and Client Secret

Additional requirements for Heroku deployment:
- Heroku CLI
- Heroku account
- Heroku Postgres add-on - in this case, you can skip the local PostgreSQL installation

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/lutencjusz/SpringDocumentationAi.git
   ```

2. Navigate to the project directory:

   ```bash
   cd SpringDocumentationAi
   ```

3. Create a new file `.env` in the root directory of the project and add the following content:

   ```bash
   OPENAI_API_KEY=YOUR_OPENAI
   OPENAI_MODEL=gpt-4o-mini
   GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID
   GOOGLE_CLIENT_SECRET=YOUR_GOOGLE_CLIENT_SECRET
   GITHUB_CLIENT_ID=YOUR_GITHUB_CLIENT_ID
   GITHUB_CLIENT_SECRET=YOUR_GITHUB_CLIENT_SECRET
   BASE_URI=http://localhost:8080
   POSTGRES_USER=your_postgres_user
   POSTGRES_PASSWORD=your_postgres_password
   POSTGRES_DB=your_postgres_db
   SOURCE_PATH=src\main\resources\docs
   MAIL_USERNAME=your_admin_mail
   MAIL_PASSWORD=your_admin_mail_password
   MAIL_SECRET=your_mail_secret
   MAIL_ADMIN=your_admin_mail to new user verification
   HEROKU_APP_NAME=your_heroku_app_name.herokuapp.com
   BASE_URI=https://your_heroku_app_name.herokuapp.com
   
   ```
4. Install the dependencies:

   ```bash
   mvn clean install
   ```

## OAuth2 GitHub Configuration

To enable login using GitHub, configure OAuth2 as follows:

1. Go to [GitHub Developer Settings](https://github.com/settings/developers) and register a new application.
2. Provide the following **Authorization callback URL**:
   ```
   http://localhost:8080/login/oauth2/code/github
   ```
3. Perform the same steps for Google OAuth2 and configure the `application.performing` 


## Running the Project

To run the project locally:

1. Start PostgreSQL and configure the database according to the `application.properties` settings.
2. Run the Spring Boot application:

   ```bash
   mvn spring-boot:run
   ```

3. The application will be available at: `http://localhost:8080`.

## Technologies Used

- **Spring Boot** - Java framework for web applications.
- **Spring Security** - Authentication and authorization.
- **OAuth2** - GitHub login integration.
- **PostgreSQL** - Relational database.
- **Thymeleaf with HXMX** - HTML forms with HTMX template engine.
- **OpenAI** - ChatGPT API.
- **Tailwind CSS** - CSS framework.
- **JWT** - JSON Web Tokens for user authentication.
- **Swagger UI** - API documentation avaible at `/swagger-ui/index.html` by ADMIN role.

## Contributing

If you wish to contribute to the project:

1. Fork the repository.
2. Create a new branch for your changes:

   ```bash
   git checkout -b your-branch-name
   ```

3. Make your changes.
4. Submit a Pull Request.

## License

This project is licensed under the terms of the [MIT License](LICENSE).
