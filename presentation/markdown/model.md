# Model
- Models will be stored in Android's built-in SQLite system.
- Model storage will be orchestrated with an ORM system such as Hibernate.


### What kind of data will be stored?
- Notifications (notification history)
  - App ID
  - App Name
  - Notification Icon
  - Notification Content
  - Other notification metadata


- Per-Application Settings
  - App ID
  - Notification setting


- Devices
  - Device UUID
  - "Friendly" name
  - Last seen time


- Settings
  - Key
  - Value
  - Last set time
