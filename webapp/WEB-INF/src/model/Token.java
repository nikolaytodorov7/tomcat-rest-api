package model;

import java.sql.Timestamp;

public class Token {
    private static final long ONE_MONTH_MILLIS = 1000L * 60 * 60 * 24 * 30;
    public int id;
    public String token;
    public int userId;
    public Timestamp dateCreated;
    public Timestamp expirationDate;

    public Token(String token, int userId) {
        this.token = token;
        this.userId = userId;
        this.dateCreated = new Timestamp(System.currentTimeMillis());
        this.expirationDate = new Timestamp(System.currentTimeMillis() + ONE_MONTH_MILLIS);
    }

    public Token(int id, String token, int userId, Timestamp created, Timestamp expires) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.dateCreated = created;
        this.expirationDate = expires;
    }
}
