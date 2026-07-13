package com.courtai;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.io.*;
public class HashGen {
    public static void main(String[] args) throws Exception {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder(12);
        String hash = enc.encode("Test@1234");
        String sql = "UPDATE users SET password_hash = '" + hash + "' WHERE email = 'advocate.test@judiciai.com';";
        new FileWriter("d:/Projects/JudiciaX/court-ai-backend/fix_advocate.sql").append(sql).close();
        System.out.println("Written: " + hash);
    }
}
