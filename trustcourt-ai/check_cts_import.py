try:
    import app.main
    print("CTS Router Import Success")
except Exception as e:
    import traceback
    traceback.print_exc()
