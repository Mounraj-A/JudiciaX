try:
    import app.main
    print("Success")
except Exception as e:
    import traceback
    traceback.print_exc()
