# Classroom Connect Android

Native Kotlin/Jetpack Compose implementation of the School Homework & Assignment Portal for the 2026-27 school year.

## Included architecture

- **Role-based Google Sign-In:** Student, Teacher, and Parent entry points. Firebase Auth receives the Google ID token; the `Users` sheet maps the Firebase UID to the role and class section.
- **Offline-first Room database:** classes, subjects, assignments, submissions, grades, messages, and users are stored locally. Student drafts are saved before upload.
- **Google Sheets gateway:** teacher assignment creation, student submission recording, grade updates, and parent-teacher messages are isolated behind `GoogleSheetsGateway` for Sheets v4 batch operations.
- **Drive submission gateway:** student files are uploaded to the class folder and the Drive URL is recorded with the submission row.
- **FCM:** topic/user subscriptions, grade/new-assignment notifications, and assignment deep links are handled by `ClassroomMessagingService`.
- **WorkManager:** retries unsynced assignment and submission rows when connectivity returns.
- **AlarmManager:** schedules local reminders for assignment deadlines.
- **Remote Config:** provides a `school_calendar` key for school events and holiday lists.
- **Compose role homes:** student pending work, teacher workload entry point, and parent progress entry point are ready for the live repositories.

## Google Sheets schema

Create one spreadsheet per class section with these tabs and headers:

```text
Users: uid | email | displayName | role | classSection | childUid
Classes: classId | name | section | teacherUid | schoolYear
Subjects: subjectId | classId | name | color
Assignments: assignmentId | classId | subjectId | title | instructions | dueAt | createdByUid | createdAt
Submissions: submissionId | assignmentId | studentUid | submittedAt | driveFileId | driveFileUrl | isLate | status
Grades: gradeId | submissionId | assignmentId | studentUid | score | maxScore | feedback | gradedAt
Messages: messageId | senderUid | recipientUid | classId | subject | body | createdAt | isRead
```

Teachers append assignment rows. Students append submission rows after Drive upload. Teachers update grades with Sheets `batchUpdate` using the stable submission row. Parents read child submissions and grades through the mapped `childUid`.

## Required setup before a real build

1. Open `classroom-connect-android` in Android Studio and let it generate/download the Gradle wrapper.
2. Create a Firebase Android app with package `com.classroomconnect.app`.
3. Add the downloaded `google-services.json` to `app/`, then apply `com.google.gms.google-services` in `app/build.gradle.kts`.
4. Replace `default_web_client_id` in `app/src/main/res/values/strings.xml` with the OAuth web client ID.
5. Enable Google Sign-In, FCM, Remote Config, Firebase Auth, and Firebase Storage.
6. Enable Google Sheets API v4 and Drive API. Configure OAuth scopes for the class owner account and replace the gateway placeholder implementations with the authorized Sheets/Drive clients.
7. Set the Remote Config `school_calendar` JSON and create FCM topics such as `class_{classId}` and `user_{uid}`.

The app source is intentionally credential-free. Real Google accounts, Sheets ownership, Drive folders, Firebase project configuration, and notification server credentials cannot be generated safely inside the repository.
