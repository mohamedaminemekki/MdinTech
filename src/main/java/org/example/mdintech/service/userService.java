    package org.example.mdintech.service;

    import org.example.mdintech.entities.User;
    import org.example.mdintech.Singleton.dbConnection;
    import org.example.mdintech.utils.PasswordVerification;
    import org.example.mdintech.utils.UserRole;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class userService implements Iservice<User> {

        private final Connection conn;

        public userService() {
            this.conn = dbConnection.getInstance().getConn();
        }

        @Override
        public boolean save(User obj) {
            if (!PasswordVerification.isStrongPassword(obj.getPassword())) {
                throw new IllegalArgumentException("Password is not strong enough.");
            }

            String hashedPassword = PasswordVerification.hashPassword(obj.getPassword());

            String query = "INSERT INTO users (CIN, Name, Email, Password, Role, Phone, Address, City, State, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dbConnection.getInstance().getConn();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, obj.getCIN());
                stmt.setString(2, obj.getName());
                stmt.setString(3, obj.getEmail());
                stmt.setString(4, hashedPassword);
                stmt.setString(5, obj.getRole().name()); // Convert Enum to String
                stmt.setString(6, obj.getPhone());
                stmt.setString(7, obj.getAddress());
                stmt.setString(8, obj.getCity());
                stmt.setString(9, obj.getState());
                stmt.setBoolean(10, obj.isStatus());

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("User saved successfully.");
                    return true; // Indicate success
                } else {
                    throw new SQLException("User could not be saved.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Database error: " + e.getMessage());
            }
        }



        @Override
        public void update(User obj) {
            String query = "UPDATE users SET Name=?, Email=?, Password=?, Role=?, Phone=?, Address=?, City=?, State=?, status=? WHERE CIN=?";
            try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {

                String hashedPassword = PasswordVerification.hashPassword(obj.getPassword());

                stmt.setString(1, obj.getName());
                stmt.setString(2, obj.getEmail());
                stmt.setString(3, hashedPassword);
                stmt.setString(4, obj.getRole().name()); // Convert Enum to String
                stmt.setString(5, obj.getPhone());
                stmt.setString(6, obj.getAddress());
                stmt.setString(7, obj.getCity());
                stmt.setString(8, obj.getState());
                stmt.setBoolean(9, obj.isStatus());
                stmt.setInt(10, obj.getCIN());

                stmt.executeUpdate();
                System.out.println("User updated successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void updateUserWithoutPassword(User obj) {
            String query = "UPDATE users SET Name=?, Email=?, Role=?, Phone=?, Address=?, City=?, State=?, status=? WHERE CIN=?";
            try (Connection conn = dbConnection.getInstance().getConn();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, obj.getName());
                stmt.setString(2, obj.getEmail());
                stmt.setString(3, obj.getRole().name());
                stmt.setString(4, obj.getPhone());
                stmt.setString(5, obj.getAddress());
                stmt.setString(6, obj.getCity());
                stmt.setString(7, obj.getState());
                stmt.setBoolean(8, obj.isStatus());
                stmt.setInt(9, obj.getCIN());

                stmt.executeUpdate();
                System.out.println("User updated successfully (without password).");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void delete(User obj) {
            String query = "DELETE FROM users WHERE CIN=?";
            try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, obj.getCIN());
                stmt.executeUpdate();
                System.out.println("User deleted successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public User findById(int id) {
            String query = "SELECT * FROM users WHERE CIN=?";
            try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new User(
                            rs.getString("Name"),
                            rs.getInt("CIN"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            UserRole.valueOf(rs.getString("Role")),
                            rs.getString("Phone"),
                            rs.getString("Address"),
                            rs.getString("City"),
                            rs.getString("State"),
                            rs.getBoolean("status"),
                            rs.getString("pathtopic"),
                            rs.getDate("birthday")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<User> findAll() {
            List<User> users = new ArrayList<>();
            String query = "SELECT * FROM users";
            try (Connection conn = dbConnection.getInstance().getConn();Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    users.add(new User(
                            rs.getString("Name"),
                            rs.getInt("CIN"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            UserRole.valueOf(rs.getString("Role")), // Convert String to Enum
                            rs.getString("Phone"),
                            rs.getString("Address"),
                            rs.getString("City"),
                            rs.getString("State"),
                            rs.getBoolean("Status"),
                            rs.getString("pathtopic"),
                            rs.getDate("birthday")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return users;
        }

        public boolean verifyPassword(String rawPassword, String hashedPassword) {
            return PasswordVerification.verifyPassword(rawPassword, hashedPassword);
        }

        public User login(String email, String password) {
            String query = "SELECT * FROM users WHERE Email=?";
            try (Connection conn = dbConnection.getInstance().getConn();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHashedPassword = rs.getString("Password");

                    if (PasswordVerification.verifyPassword(password, storedHashedPassword)) {
                        return new User(
                                rs.getString("Name"),
                                rs.getInt("CIN"),
                                rs.getString("Email"),
                                storedHashedPassword, // Store hashed password, not plain text
                                UserRole.valueOf(rs.getString("Role")), // Convert String to Enum
                                rs.getString("Phone"),
                                rs.getString("Address"),
                                rs.getString("City"),
                                rs.getString("State"),
                                rs.getString("pathtopic"),
                                rs.getDate("birthday")
                        );
                    } else {
                        System.out.println("Incorrect password.");
                    }
                } else {
                    System.out.println("User not found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null; // Return null if login fails
        }

        public void updateUserStatus(int cin, boolean newStatus) {
            String query = "UPDATE users SET status=? WHERE CIN=?";
            try (Connection conn = dbConnection.getInstance().getConn();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setBoolean(1, newStatus);
                stmt.setInt(2, cin);

                stmt.executeUpdate();
                System.out.println("User status updated successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public List<User> searchUsers(Integer minAge, Integer maxAge, String name, Integer cin, String address) {
            List<User> users = new ArrayList<>();
            String query = "SELECT * FROM users WHERE 1=1";

            if (minAge != null) query += " AND Age >= ?";
            if (maxAge != null) query += " AND Age <= ?";
            if (name != null && !name.isEmpty()) query += " AND Name LIKE ?";
            if (cin != null) query += " AND CIN = ?";
            if (address != null && !address.isEmpty()) query += " AND Address LIKE ?";

            try (Connection conn = dbConnection.getInstance().getConn();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                int index = 1;
                if (minAge != null) stmt.setInt(index++, minAge);
                if (maxAge != null) stmt.setInt(index++, maxAge);
                if (name != null && !name.isEmpty()) stmt.setString(index++, "%" + name + "%");
                if (cin != null) stmt.setInt(index++, cin);
                if (address != null && !address.isEmpty()) stmt.setString(index++, "%" + address + "%");

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    users.add(new User(
                            rs.getString("Name"),
                            rs.getInt("CIN"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            UserRole.valueOf(rs.getString("Role")),
                            rs.getString("Phone"),
                            rs.getString("Address"),
                            rs.getString("City"),
                            rs.getString("State"),
                            rs.getBoolean("status"),
                            rs.getString("pathtopic"),
                            rs.getDate("birthday")
                    ));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return users;
        }

        public boolean doesEmailExist(String email) {
            String query = "SELECT * FROM users WHERE Email=?";
            try (Connection conn = dbConnection.getInstance().getConn();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }

        public void updatePassword(String email , String newPassword) {
            String query = "UPDATE users SET Password=? WHERE Email=?";
            try (Connection conn = dbConnection.getInstance().getConn();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                String hashedpassword=PasswordVerification.hashPassword(newPassword);
                stmt.setString(1, hashedpassword);
                stmt.setString(2, email);
                stmt.executeUpdate();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public User findByEmail(String email) {
            String query = "SELECT * FROM users WHERE email=?";
            try (Connection conn = dbConnection.getInstance().getConn();PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new User(
                            rs.getString("Name"),
                            rs.getInt("CIN"),
                            rs.getString("Email"),
                            rs.getString("Password"),
                            UserRole.valueOf(rs.getString("Role")),
                            rs.getString("Phone"),
                            rs.getString("Address"),
                            rs.getString("City"),
                            rs.getString("State"),
                            rs.getBoolean("status"),
                            rs.getString("pathtopic"),
                            rs.getDate("birthday")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
