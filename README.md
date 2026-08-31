# AgriLink AI — Complete Java Full-Stack Working Model

This project contains the complete frontend, Java backend and database configuration in one folder. The original HTML/CSS/JavaScript interface is served by Spring Boot, so only one application needs to run or be deployed.

## Included features

- Sign Up using name, 10-digit phone number, district and password
- Sign In using phone number as the username and password
- BCrypt password hashing (plain passwords are never stored)
- JWT login session, automatic session restoration and Sign Out
- Per-user login count and last-login timestamp
- Per-user database history for every service input and result
- Automatic device location request after Sign Up, plus a location refresh button
- Latitude, longitude and a readable detected place stored in the user's database record
- Clear units: kilograms, acres, days, rupees and yield as number of 100 kg bags
- Shared Transport
- Buyer Reliability
- True Profit Calculator
- Rescue My Harvest
- Oversupply Map
- Harvest-Time Advisor
- Crop Details
- Automatic Buyer Matching
- Buyer, crop listing, transport and rescue CRUD APIs
- H2 file database for an immediate local demonstration
- MySQL profile and SQL file for persistent production deployment
- Docker and Railway deployment configuration

## Folder structure

```text
AgriLink-AI-FullStack/
├── Dockerfile
├── pom.xml
├── railway.json
├── database/
│   └── agrilink-mysql.sql
└── src/
    ├── main/
    │   ├── java/com/agrilink/       Java backend
    │   └── resources/
    │       ├── application.properties
    │       ├── application-mysql.properties
    │       └── static/              HTML, CSS and JavaScript frontend
    └── test/                         Automated integration test
```

Do not move `index.html`, `style.css` or `script.js`. Spring Boot serves them from `src/main/resources/static`.

## Method 1: easiest local run

If `AgriLink-AI.jar` is included, open PowerShell in the project folder and run:

```powershell
java -jar AgriLink-AI.jar
```

Open <http://localhost:8080>. The default H2 database is created automatically in `data/agrilink`.

## Method 2: run from source in VS Code

Install:

1. Java JDK 21 or newer
2. Apache Maven
3. VS Code
4. Microsoft's **Extension Pack for Java** in VS Code

Then:

1. Open the complete `AgriLink-AI-FullStack` folder in VS Code.
2. Select **Terminal → New Terminal**.
3. Run:

```powershell
mvn spring-boot:run
```

4. Wait for `Started AgriLinkApplication`.
5. Open <http://localhost:8080>.

Do not double-click `index.html`; that bypasses the Java backend.

## How to use the website

### First-time user

1. Select **Sign Up**.
2. Enter your name, 10-digit phone number, district and a password containing at least six characters.
3. Confirm the password and press **Create Account**.
4. The browser asks for location permission. Select **Allow** to detect and save the device location.
5. If the prompt does not appear, select **Detect My Location**.
6. Select your language and continue to the dashboard.

### Returning user

1. Select **Sign In**.
2. Enter the registered phone number and password.
3. Press **Sign In**.

The phone number is the username. The password is stored only as a BCrypt hash.

### Using a farming service

1. Select any of the eight dashboard cards.
2. Complete its form.
3. Press **Show Result**.
4. Java calculates the result from database and input data.
5. The user ID, service, submitted values, result and time are saved in `analysis_history`.

The forms use these units:

- Crop or transport quantity: **kg**
- Land size: **acres**
- Expected yield: **number of 100 kg bags** (for example, enter `45` for 45 bags)
- Crop selling/minimum price: **₹ per 100 kg bag**
- Total farming cost: **₹**
- Freshness and harvest time remaining: **days**

Detected location is automatically inserted into relevant district, pickup and farm-location fields. The user can change the inserted text before submitting a form.

Use **Sign Out** in the dashboard header to end the browser session.

## View the local H2 database

While the application is running, open <http://localhost:8080/h2-console> and enter:

```text
JDBC URL: jdbc:h2:file:./data/agrilink
User Name: sa
Password: (leave empty)
```

Useful SQL:

```sql
SELECT id, name, mobile, district, latitude, longitude, detected_location,
       role, created_at, last_login_at, login_count
FROM users;
SELECT * FROM analysis_history ORDER BY created_at DESC;
SELECT * FROM buyers;
SELECT * FROM crop_listings;
SELECT * FROM transport_requests;
SELECT * FROM rescue_requests;
```

## Use an existing local MySQL database

MySQL 8 or newer is recommended.

1. Open MySQL Workbench.
2. Run `database/agrilink-mysql.sql`, or let Spring create/update the tables automatically.
3. Open PowerShell in the project folder.
4. Set the connection values and start the project:

```powershell
$env:SPRING_PROFILES_ACTIVE="mysql"
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="agrilink"
$env:DB_USER="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
$env:JWT_SECRET="replace-this-with-a-long-random-secret-of-at-least-32-characters"
mvn spring-boot:run
```

Change the five `DB_*` values to match your database. Never upload a real database password or JWT secret to GitHub.

## Build the project

Run automated tests:

```powershell
mvn test
```

Create a runnable JAR:

```powershell
mvn package
java -jar target/agrilink-ai-1.0.0.jar
```

## Deploy the complete website on Railway

Railway can deploy the bundled frontend and Java backend together using the supplied `Dockerfile`, with a MySQL service in the same Railway project.

### 1. Upload to GitHub

1. Create a new empty GitHub repository, for example `agrilink-ai`.
2. Do not upload `.env`, `data/` or `target/`.
3. From the project folder run:

```powershell
git init
git add .
git commit -m "Complete AgriLink AI working model"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/agrilink-ai.git
git push -u origin main
```

If Git asks you to authenticate, complete GitHub's sign-in flow.

### 2. Create the Railway project and MySQL database

1. Sign in at <https://railway.com>.
2. Create a **New Project**.
3. Select **Add Service → Database → MySQL**.
4. Keep the database private; the Java service can reach it inside the same Railway project.

### 3. Deploy the Java/full-stack service

1. In the same Railway project select **Add Service → GitHub Repo**.
2. Choose the `agrilink-ai` repository.
3. Railway detects the supplied `Dockerfile` and builds the application.
4. Open the application service's **Variables** tab and add:

```text
SPRING_PROFILES_ACTIVE=mysql
DB_HOST=${{MySQL.MYSQLHOST}}
DB_PORT=${{MySQL.MYSQLPORT}}
DB_NAME=${{MySQL.MYSQLDATABASE}}
DB_USER=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
JWT_SECRET=PASTE_A_LONG_RANDOM_SECRET_HERE
```

If your Railway database service has a different name, replace `MySQL` in the references with its exact service name. Set `JWT_SECRET` to a private random value of at least 32 characters.

### 4. Create the public website URL

1. Open the Java service's **Settings**.
2. Find **Networking → Public Networking**.
3. Select **Generate Domain**.
4. Open the generated `https://...up.railway.app` address.

This generated HTTPS address is the public shared link. Send it to anyone and they can open the site on a phone or computer. HTTPS is also required by browsers for device-location permission. The frontend calls `/api` on the same domain, so no frontend URL changes or CORS configuration are required. Future pushes to the connected GitHub branch trigger new deployments.

### 5. Deployment health check

`railway.json` configures this health endpoint:

```text
/api/public/health
```

Opening `https://YOUR_DOMAIN/api/public/health` should return a JSON response with `"status":"UP"`.

### 6. Make it findable through Google Search

The shared Railway link works immediately after a successful deployment. Appearing in Google search is a separate process and is not instant or guaranteed.

1. Open [Google Search Console](https://search.google.com/search-console/).
2. Add the generated Railway URL as a **URL-prefix property**. A custom domain such as `agrilink.example.com` is better for a permanent project, but is optional for the hackathon.
3. Complete the ownership verification shown by Search Console.
4. Submit `https://YOUR_DOMAIN/sitemap.xml` in **Sitemaps**.
5. Open **URL Inspection**, enter `https://YOUR_DOMAIN/`, and select **Request indexing**.
6. Keep the public site running. Google may take several days or weeks to crawl it.

This project now serves `/robots.txt` and `/sitemap.xml` automatically for whichever public domain is used, and includes search-description metadata in `index.html`.

## View data in the deployed Railway MySQL database

Do not use the H2 console after production deployment; the Railway configuration uses MySQL.

### Railway dashboard

1. Open the Railway project.
2. Select the **MySQL** service, not the Java application service.
3. Open its database/data view if shown in the Railway dashboard.
4. Inspect the `users`, `analysis_history`, `transport_requests`, `rescue_requests`, `crop_listings` and `buyers` tables.

### MySQL Workbench

1. In the Railway MySQL service open **Settings → Networking → Public Networking** and enable its TCP proxy/public connection.
2. Open the service **Variables** and locate the public MySQL connection values or `MYSQL_PUBLIC_URL`.
3. In MySQL Workbench create a connection using the host, public port, username, password and database name from those Railway values.
4. Test the connection, open a SQL tab and run:

```sql
SELECT id, name, mobile, district, latitude, longitude, detected_location,
       created_at, last_login_at, login_count
FROM users
ORDER BY created_at DESC;

SELECT id, user_id, service_type, request_json, result_json, created_at
FROM analysis_history
ORDER BY created_at DESC;
```

Keep the public database password secret. Disable public database networking when you no longer need Workbench access; the Java service can continue using Railway's private database network.

## Automatic location behavior

- Location detection starts after a new account is created, and the user can also select the dashboard location button later.
- Browsers always require the person using the device to approve location access; a website cannot legally or technically bypass that prompt.
- Production location detection works on the Railway HTTPS link. Local testing works on `localhost`.
- The backend stores `latitude`, `longitude` and `detected_location` in `users`.
- The backend attempts to convert coordinates to a readable village/district/state. If that map service is temporarily unavailable, the coordinates are still saved and shown.
- If permission was denied, open the browser's site settings, allow **Location**, reload the page and press the location button again.

## Production notes

- Use Railway MySQL instead of the default H2 database because container files can be replaced during deployments.
- Back up the MySQL database before an important demonstration.
- Keep `JWT_SECRET` and database passwords only in deployment variables.
- This working model does not implement SMS OTP, payments or government identity verification.
- HTTPS is provided by Railway for its generated and custom domains.
- Device location is personal data. Tell hackathon users why it is collected and do not share the database credentials.

## Main API operations

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
POST /api/location/detect

POST /api/services/{0-7}/analyze
GET  /api/services/history

GET/POST/PUT/DELETE /api/buyers
GET/POST/PUT/DELETE /api/crops
GET/POST/PUT/DELETE /api/transport
GET/POST/PUT/DELETE /api/rescue
```

Protected requests include the JWT in `Authorization: Bearer <token>`. The supplied frontend handles this automatically.
