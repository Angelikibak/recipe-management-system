Recipe Management System – Οδηγίες Εκτέλεσης (Run)

1) Απαιτήσεις
- Java JDK 17
- IntelliJ IDEA (ή άλλο IDE για Java)

2) Άνοιγμα Project
- Ανοίξτε το project στο IntelliJ (Open -> επιλέξτε τον φάκελο του project).
- Περιμένετε να ολοκληρωθεί το indexing / download dependencies (αν ζητηθεί).

3) Ρύθμιση Java Version
- File -> Project Structure -> Project SDK: Java 17
- Project language level: 17

4) Εκτέλεση Εφαρμογής
- Κάντε Run την κλάση:
  recipes.Main

Με την εκκίνηση:
- Αρχικοποιείται η βάση SQLite (αν δεν υπάρχει δημιουργείται)
- Δημιουργούνται οι πίνακες από το schema.sql
- Ανοίγει το Swing γραφικό περιβάλλον της εφαρμογής

Σημείωση: Το project είναι Maven-based και τα dependencies
κατεβαίνουν αυτόματα κατά το άνοιγμα στο IntelliJ.

5) Βάση Δεδομένων (σύντομα)
Η εφαρμογή χρησιμοποιεί SQLite (τοπικό αρχείο recipes.db) και δεν απαιτεί
MySQL server, ούτε χρήστες/password.
Περισσότερες λεπτομέρειες υπάρχουν στο αρχείο README_DATABASE.txt.

6) Βίντεο Επίδειξης (Π2.4)
Ο σύνδεσμος του βίντεο βρίσκεται στο αρχείο:
- συ