# Notes Import Application

## Description

This application performs scheduled import of patient notes from an external legacy system into a modern database.

It executes the following logic:

- Retrieves a list of clients from the legacy system.
- For each client, loads their associated notes.
- Finds corresponding patients in the new system using clientGuid.
- If the patient exists and is active, the note is either:
    - Inserted (if not previously imported), or
    - Updated (if the new version has a more recent modifiedDateTime).
- Automatically creates a new user (CompanyUser) if it doesn't exist based on the note's author (loggedUser).
- Skips notes for unknown or inactive patients.

## Build and Run

### Build with Maven

```bash
mvn clean package
```
After a successful build, the JAR will be located in the target/ directory.

### Run from the command line
```bash
java -jar target/cleverdev-task-1.jar
```

## Testing
### Run unit tests with:
```bash
mvn test
```