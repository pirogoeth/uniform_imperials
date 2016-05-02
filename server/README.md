unwind
======

**Uniform Imperials Notification Dispatcher**


setup
=====

```
virtualenv .
source bin/activate
pip install -r requirements.txt
pip install -U malibu
pip install -U bottle
pip install -U raven
pip install -U restify
pip install -e .
unwind-server --config=unwind.ini
```
