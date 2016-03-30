# Model
- Models will be stored in Android's built-in SQLite system.
- Model storage will be orchestrated with an ORM system such as Hibernate.


### Model Style
All of our data can not be represented by database models — some if it, we *do not* want to represent in a database.
Devices can be relatively transient and storing them in a database locally can prove problematic while synching up.

Models will be written in several styles:
- Pure Database/ORM ~> Only stored locally in the on-device database.
- Pure HTTP/RESTful ~> Only retrieved from the REST API, cached in memory during app run.
- Hybrid ORM+REST ~> Updated from REST API, but cached to local database.


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
