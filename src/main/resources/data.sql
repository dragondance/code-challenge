INSERT INTO account (iban, ammount) VALUES
  ('ES6220389439049641732642', 2443324),
  ('ES3714658286232352148849', 243),
  ('ES3320387463119933926654', 2423526);

 INSERT INTO transaction (reference, account_iban, date, ammount, fee, description) VALUES
 ('12345A', 'ES6220389439049641732642', '2019-07-16T16:55:42.000Z', 6.38, 3.18, 'Restaurant payment')
