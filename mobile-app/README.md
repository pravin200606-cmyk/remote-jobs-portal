# RemoteWork Hub — Mobile App (Capacitor)

## 📱 Project Structure
```
mobile-app/
├── capacitor.config.json      # Capacitor config (appId, webDir, plugins)
├── package.json               # Dependencies (Capacitor 6 + 14 plugins)
├── android/                   # Generated Android native project (open in Android Studio)
└── www/                       # Web app bundle (source of truth)
    ├── index.html             # SPA shell with bottom nav + drawer
    ├── css/app.css            # Dark glassmorphism theme
    └── js/
        ├── app.js             # Router, auth, navigation
        ├── api.js             # REST client → Spring Boot :8080
        └── pages/
            ├── home.js        # Hero, stats, latest jobs
            ├── jobs.js        # Job listing with filters
            ├── job-detail.js  # Job detail + Apply button
            ├── login.js       # Login form
            ├── register.js    # Register form (seeker/employer)
            ├── profile.js     # Profile editor
            ├── applications.js# Application tracker
            ├── dashboard.js   # Role-based dashboard
            ├── post-job.js    # Employer job posting
            └── admin.js       # Admin panel
```

## 🚀 How to Run

### Step 1 — Start Spring Boot backend
```bash
cd remotejobs
./mvnw spring-boot:run
# Backend runs at http://localhost:8080
```

### Step 2 — Open in Android Studio
```bash
cd mobile-app
npx cap open android
```
Then click **Run ▶** in Android Studio.

### Step 3 — On a real Android device
Edit `www/js/api.js` — change `10.0.2.2` to your PC's LAN IP (e.g. `192.168.1.5`):
```js
const BASE = 'http://192.168.1.5:8080';
```
Then re-sync:
```bash
npx cap sync
```

## 📡 REST API Endpoints (added to Spring Boot)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/auth/login` | Public | Login |
| POST | `/api/auth/register` | Public | Register |
| GET | `/api/auth/me` | Session | Current user |
| GET | `/api/jobs` | Public | List/search jobs |
| GET | `/api/jobs/{id}` | Public | Job detail |
| POST | `/api/jobs/{id}/apply` | JOBSEEKER | Apply for job |
| GET | `/api/jobseeker/applications` | JOBSEEKER | My applications |
| GET/PUT | `/api/jobseeker/profile` | JOBSEEKER | View/edit profile |
| GET/POST | `/api/employer/jobs` | EMPLOYER | Manage jobs |
| DELETE | `/api/employer/jobs/{id}` | EMPLOYER | Delete job |
| GET | `/api/admin/stats` | ADMIN | Platform stats |
| GET | `/api/admin/users` | ADMIN | All users |

## 📦 Capacitor Plugins Included
- App, Browser, Camera, Device, Filesystem
- Haptics, Keyboard, Network, Preferences
- Push Notifications, Share, SplashScreen, StatusBar, Toast

## 🔧 After any `www/` changes
```bash
npx cap sync
```
