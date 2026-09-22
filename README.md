# Jan Sahayata (जन सहायता) - Citizen Scheme & Direct Benefit Assistance Portal

A complete, production-grade Citizen Assistance Scheme & Direct Benefit Transfer (DBT) portal built with Kotlin, Jetpack Compose, Room Database, and Material 3 design.

---

## 🏛️ Project Architecture & Highlights

- **Original Civic Theme:** Custom Jan Sahayata emblem, Saffron/Navy/Emerald palette compliant with Indian government service design principles.
- **Bilingual (English + Hindi):** Instant language switcher on every screen (`EN` / `हिंदी`).
- **End-to-End Workflow:**
  1. **Citizen Portal:** Home landing page, Services, Eligibility rules, FAQs, Guidelines, Disclaimer.
  2. **Citizen Registration:** Name, Mobile, Email, State, District, Address, Aadhaar last 4 digits only (masked), Consent checkbox, and 6-digit OTP verification flow.
  3. **Citizen Login & Session:** Mobile/Email + hashed password authentication, session management, Forgot Password.
  4. **Citizen Dashboard:** My Applications, New Application CTA, Draft Applications, Submitted Applications, Status Tracker, Profile.
  5. **7-Step Application Wizard:**
     - Step 1: Applicant Details (Name, Parent, DOB, Gender, Category, Mobile, Address, District, Block, PIN)
     - Step 2: Beneficiary Details (Child Name, DOB, Gender, Birth Reg No, Relationship, School/Class)
     - Step 3: Bank Details (Account Holder, Bank, Branch, Account No, IFSC with live verification)
     - Step 4: Eligibility Check (Rule-based calculator with official disclaimer)
     - Step 5: Document Upload (Identity, Birth Cert, Address, Passbook, Photo with format & size validation)
     - Step 6: Review & Declaration (Summary with Edit shortcuts, declaration, legal consent)
     - Step 7: Submission & Acknowledgement (Unique `JS-2026-XXXXXX` number, printable slip, share sheet)
  6. **Application Status Tracking:** Timeline with dates, statuses (`DRAFT`, `SUBMITTED`, `UNDER_REVIEW`, `DOCUMENT_VERIFICATION`, `APPROVED`, `REJECTED`, `RETURNED_FOR_CORRECTION`), and officer remarks.
  7. **Edit Application:** Permitted strictly for `DRAFT` and `RETURNED_FOR_CORRECTION`.
  8. **Officer / Admin Portal:**
     - Pre-seeded admin: `admin@jansahayata.gov.in` / `Admin@123`
     - Full application registry with district & status filtering
     - Search by Application Number or Mobile
     - Scrutiny & document verification (Verified / Discrepancy Found)
     - Action workflow: Move to Review, Verify Docs, Approve, Reject, or Return for Correction with specific notes
     - Audit Log trail with timestamp and officer credentials
  9. **Security Compliance:** SHA-256 salted password hashing, Aadhaar masking (never stores full 12 digits), validation for Indian mobile/IFSC/PIN, role-based protection.

---

## 🗄️ Database Schema & ORM

### Room (Android Local Database)
- `users`: Citizen credentials, hashed passwords, profile details
- `applications`: Core application master records, statuses, submission timestamps
- `beneficiaries`: Child/beneficiary details linked by `applicationId`
- `bank_details`: Bank IFSC, account number, branch, verification flag
- `documents`: Uploaded document metadata, sizes, types, verification state
- `status_history`: Chronological audit trail of all status transitions
- `admins`: Administrative officer credentials and department assignment
- `audit_logs`: Detailed action logs with timestamps and officer IDs

### Prisma Backend Schema (PostgreSQL Reference)
See `/prisma/schema.prisma` for the matching server-side schema.

---

## 🚀 Database Migration & Seed Commands

For running the complementary Prisma / Node backend:

```bash
# 1. Install dependencies
npm install prisma @prisma/client

# 2. Run migrations
npx prisma migrate dev --name init_jan_sahayata

# 3. Generate Prisma client
npx prisma generate

# 4. Seed admin account and demo applications
npx ts-node prisma/seed.ts
```

### Environment Variables (`.env`)
```env
DATABASE_URL="postgresql://jansahayata:secure_pass@localhost:5432/jan_sahayata_db?schema=public"
ADMIN_DEFAULT_EMAIL="admin@jansahayata.gov.in"
ADMIN_DEFAULT_PASSWORD="Admin@123"
ADMIN_DEFAULT_NAME="Dr. Rajesh Kumar Sharma"
JWT_SECRET="jansahayata-jwt-super-secret-key-2026"
PORT=8080
```

---

## 🌐 API Routes Architecture

| Method | Route | Description | Auth Required |
|--------|-------|-------------|---------------|
| `POST` | `/api/v1/auth/register` | Register citizen with OTP verification | Public |
| `POST` | `/api/v1/auth/login` | Citizen login with mobile/email & password | Public |
| `POST` | `/api/v1/auth/forgot-password` | Request password reset via OTP | Public |
| `POST` | `/api/v1/admin/login` | Officer / Admin login | Public |
| `GET`  | `/api/v1/applications` | List citizen's applications | Bearer (Citizen) |
| `POST` | `/api/v1/applications` | Create or draft new application | Bearer (Citizen) |
| `GET`  | `/api/v1/applications/:id` | Get application details & timeline | Bearer |
| `PUT`  | `/api/v1/applications/:id` | Edit draft or returned application | Bearer (Citizen) |
| `POST` | `/api/v1/applications/:id/documents` | Upload verified proof document | Bearer (Citizen) |
| `GET`  | `/api/v1/admin/applications` | Officer filterable application registry | Bearer (Admin) |
| `PATCH`| `/api/v1/admin/applications/:id/status` | Change application status & add remarks | Bearer (Admin) |
| `PATCH`| `/api/v1/admin/documents/:id/verify` | Verify individual document | Bearer (Admin) |
| `GET`  | `/api/v1/admin/audit-logs` | Retrieve chronological audit logs | Bearer (Admin) |

---

## 🏭 Production Deployment Instructions

1. **Android Client Build:**
   ```bash
   gradle :app:assembleRelease
   ```
2. **Server-Side Deployment (Docker / Kubernetes / Cloud Run):**
   ```dockerfile
   FROM node:20-alpine
   WORKDIR /app
   COPY package*.json ./
   RUN npm ci --only=production
   COPY prisma ./prisma/
   RUN npx prisma generate
   COPY . .
   EXPOSE 8080
   CMD ["node", "dist/server.js"]
   ```
3. Configure SSL/TLS termination, rate limiting at reverse proxy (Nginx/Cloudflare), and strict CORS headers.
