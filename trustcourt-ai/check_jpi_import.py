try:
    import app.main
    print("JPI Router Import Success")
except Exception as e:
    import traceback
    traceback.print_exc()
