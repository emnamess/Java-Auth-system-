package esprit.tn.services;

import esprit.tn.main.DatabaseConnection;
import java.sql.*;
import java.time.Instant;

public class BlockingService {
    private Connection cnx;

    public BlockingService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    // 🚀 Check if user is blocked
    public boolean isUserBlocked(String email) {
        String query = "SELECT blocked_until FROM user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp blockedUntil = rs.getTimestamp("blocked_until");
                System.out.println("📌 Retrieved blocked_until: " + blockedUntil);
                return blockedUntil != null && blockedUntil.toInstant().isAfter(Instant.now());
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error checking if user is blocked: " + e.getMessage());
        }
        return false;
    }


    // ❌ Handle failed login attempt
    public void incrementFailedAttempts(String email) {
        String query = "SELECT failed_attempts FROM user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int failedAttempts = rs.getInt("failed_attempts") + 1;
                System.out.println("❌ Retrieved failed attempts: " + (failedAttempts - 1));
                System.out.println("⚠️ Checking failed attempts: " + failedAttempts);

                if (failedAttempts >= 3) {
                    System.out.println("⛔ Too many failed attempts! Calling blockUser()...");
                    blockUser(email);
                } else {
                    updateFailedAttempts(email, failedAttempts);
                }
            } else {
                System.out.println("⚠️ No user found with email: " + email);
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error incrementing failed attempts: " + e.getMessage());
        }
    }

    // ⏳ Block user for 15 minutes
    private void blockUser(String email) {
        String query = "UPDATE user SET failed_attempts = 3, blocked_until = ? WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            Timestamp blockTimestamp = Timestamp.from(Instant.now().plusSeconds(900)); // 15 minutes
            System.out.println("🕒 Setting blocked_until for " + email + " to: " + blockTimestamp);
            stmt.setTimestamp(1, blockTimestamp);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("✅ Rows updated: " + rowsUpdated);

            if (rowsUpdated == 0) {
                System.out.println("⚠️ User block update failed! Verify email.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error blocking user: " + e.getMessage());
        }
    }

    // 🔄 Reset failed attempts
    public void resetFailedAttempts(String email) {
        String query = "UPDATE user SET failed_attempts = 0, blocked_until = NULL WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("✅ Failed attempts reset for " + email);
            System.out.println("✅ Rows updated: " + rowsUpdated);
        } catch (SQLException e) {
            System.err.println("⚠️ Error resetting failed attempts: " + e.getMessage());
        }
    }

    // 🔄 Update failed attempts
    private void updateFailedAttempts(String email, int failedAttempts) {
        String query = "UPDATE user SET failed_attempts = ? WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, failedAttempts);
            stmt.setString(2, email);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("❌ Failed attempt recorded: " + failedAttempts);
            System.out.println("✅ Rows updated: " + rowsUpdated);
        } catch (SQLException e) {
            System.err.println("⚠️ Error updating failed attempts: " + e.getMessage());
        }
    }

    // ⏳ Get lockout expiration time
    public long getBlockedUntil(String email) {
        String query = "SELECT blocked_until FROM user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp blockedUntil = rs.getTimestamp("blocked_until");
                System.out.println("📌 Retrieved blocked_until: " + blockedUntil);
                if (blockedUntil != null) {
                    return blockedUntil.getTime();
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error getting blocked_until: " + e.getMessage());
        }
        return 0;
    }

    public int getFailedAttempts(String email) {
        String query = "SELECT failed_attempts FROM user WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int attempts = rs.getInt("failed_attempts");
                System.out.println("📌 Retrieved failed_attempts: " + attempts);
                return attempts;
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error getting failed attempts: " + e.getMessage());
        }
        return 0;
    }
}
