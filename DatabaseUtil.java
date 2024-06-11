package TaskTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DatabaseUtil {
	
    private static final String URL = "jdbc:mysql://localhost:3306/UserInformation"; # where you save data
    private static final String USER = "root"; # user
    private static final String PASSWORD = "2024javaSystemPD"; #user's password

    // get connect with database
    private static Connection Connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    
    // Placeholder for storing users and tasks in-memory
    public static List<User> userList = new ArrayList<>();
    public static List<TaskAssignment> assignData = new ArrayList<>();
    public static List<Task> taskData = new ArrayList<>();


    public synchronized static boolean AddTaskAssignments(String username, List<TaskAssignment> taskAssignments) {
        for(int i = 0;i < taskAssignments.size(); i++){
            String sql = "INSERT INTO UserData(name, username, creator, userIDs, status, year, month, day, content, notificationYear, notificationMonth, notificationDay) VALUES(? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
                TaskAssignment assigntask = taskAssignments.get(i);
                pstmt.setString(1, assigntask.getName());
                pstmt.setString(2, username);
                pstmt.setString(3, assigntask.getTaskAssigner());
                pstmt.setString(4, "");
                pstmt.setString(5, assigntask.getStatus());
                pstmt.setInt(6, assigntask.getYear());
                pstmt.setInt(7, assigntask.getMonth());
                pstmt.setInt(8, assigntask.getDay());
                pstmt.setString(9, assigntask.getContent());
                pstmt.setInt(10, assigntask.getNotificationYear());
                pstmt.setInt(11, assigntask.getNotificationMonth());
                pstmt.setInt(12, assigntask.getNotificationDay());
                pstmt.executeUpdate();
            }catch(SQLException e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Fetches new task assignments from the database. 
     * @param username The username of the user whose data be requested
     * @return List of new TaskAssignment objects for the user.
     */
    public synchronized static List<TaskAssignment> getNewTaskAssignments(String username) {
        // Placeholder for actual database logic
        List<TaskAssignment> retuAssignments = new ArrayList<>();
        String sql = "SELECT id, name, creator, status, year, month, day, content, notificationYear, notificationMonth, notificationDay FROM UserData WHERE username = ? AND username != creator";
        try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    TaskAssignment task = new TaskAssignment(rs.getString("name"), rs.getString("status"), rs.getInt("year"), rs.getInt("month"), rs.getInt("day"), rs.getString("content"), rs.getInt("notificationYear"), rs.getInt("notificationMonth"), rs.getInt("notificationDay"), rs.getString("creator"));
                    retuAssignments.add(task);
                    String sql1 = "DELETE FROM UserData WHERE id = ?";
                    try(Connection conn1 = Connect(); PreparedStatement pstmt1 = conn1.prepareStatement(sql1)){
                        pstmt1.setInt(1, rs.getInt("id"));
                        pstmt1.executeUpdate();
                    }catch(SQLException e1){
                        System.out.println(e1.getMessage());
                    }
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return retuAssignments;
    }

    /**
     * Assigns a task to a list of users.
     * @param task The task to be assigned.
     * @param userIds List of user IDs to whom the task is assigned.
     * @return true if the assignment was successful, false otherwise.
     */
    public synchronized static boolean assignTaskToUsers(TaskAssignment task, List<String> userIds) {
        // Placeholder for actual database logic
        for(int i = 0;i < userIds.size(); i++){
            String sql = "INSERT INTO UserData(name, username, creator, userIDs, status, year, month, day, content, notificationYear, notificationMonth, notificationDay) VALUES(? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, task.getName());
                pstmt.setString(2, userIds.get(i));
                pstmt.setString(3, task.getTaskAssigner());
                pstmt.setString(4, "");
                pstmt.setString(5, task.getStatus());
                pstmt.setInt(6, task.getYear());
                pstmt.setInt(7, task.getMonth());
                pstmt.setInt(8, task.getDay());
                pstmt.setString(9, task.getContent());
                pstmt.setInt(10, task.getNotificationYear());
                pstmt.setInt(11, task.getNotificationMonth());
                pstmt.setInt(12, task.getNotificationDay());
                pstmt.executeUpdate();
            }catch(SQLException e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Updates the tasks in the database for a specific user.
     * @param username The username of the user whose tasks are being updated.
     * @param tasks List of tasks to be updated.
     * @return true if the update was successful, false otherwise.
     */
    public synchronized static boolean updateTasksInDatabase(String username, List<Task> tasks) {
        ArrayList<Integer> taskIDs = new ArrayList<>();
        // Placeholder for actual database logic
        String sql = "SELECT id FROM UserData WHERE username = ? AND username = creator";
        try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    taskIDs.add(rs.getInt("id"));
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
        for(int i = 0; i < Math.min(taskIDs.size(), tasks.size()); i++){
            Task task = tasks.get(i);
            int taskID = taskIDs.get(i);
            sql = "UPDATE UserData SET name = ?, status = ?, year = ?, month = ?, day = ?, content = ?, notificationYear = ?, notificationMonth = ?, notificationDay = ?, userIDs = ? WHERE id = ?";
            try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, task.getName());
                pstmt.setString(2, task.getStatus());
                pstmt.setInt(3, task.getYear());
                pstmt.setInt(4, task.getMonth());
                pstmt.setInt(5, task.getDay());
                pstmt.setString(6, task.getContent());
                pstmt.setInt(7, task.getNotificationYear());
                pstmt.setInt(8, task.getNotificationMonth());
                pstmt.setInt(9, task.getNotificationDay());
                pstmt.setString(10, String.join(",", task.getUserIDs()));
                pstmt.setInt(11, taskID);
                pstmt.executeUpdate();
            }catch(SQLException e){
                System.out.println(e.getMessage());
                return false;
            }
        }
        if(taskIDs.size() < tasks.size()){
            for(int i = taskIDs.size(); i < tasks.size(); i++){
                sql = "INSERT INTO UserData(name, username, creator, userIDs, status, year, month, day, content, notificationYear, notificationMonth, notificationDay) VALUES(? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
                    Task task = tasks.get(i);
                    pstmt.setString(1, task.getName());
                    pstmt.setString(2, username);
                    pstmt.setString(3, username);
                    pstmt.setString(4, String.join(",", task.getUserIDs()));
                    pstmt.setString(5, task.getStatus());
                    pstmt.setInt(6, task.getYear());
                    pstmt.setInt(7, task.getMonth());
                    pstmt.setInt(8, task.getDay());
                    pstmt.setString(9, task.getContent());
                    pstmt.setInt(10, task.getNotificationYear());
                    pstmt.setInt(11, task.getNotificationMonth());
                    pstmt.setInt(12, task.getNotificationDay());
                    pstmt.executeUpdate();
                }catch(SQLException e){
                    System.out.println(e.getMessage());
                    return false;
                }
            }
        }else if(taskIDs.size() > tasks.size()){
            for(int i = tasks.size(); i < taskIDs.size(); i++){
                sql = "DELETE FROM UserData WHERE id = ?";
                try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
                    pstmt.setInt(1, taskIDs.get(i));
                    pstmt.executeUpdate();
                }catch(SQLException e){
                    System.out.println(e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Initializes the task data for a user from the database.
     * @param username The username of the user.
     * @return A string representation of the user's tasks.
     */
    public synchronized static String initializeUserData(String username) {
        // Placeholder for actual database logic
        String output_string = "";
        String sql = "SELECT name, creator, userIDs, status, year, month, day, content, notificationYear, notificationMonth, notificationDay FROM UserData WHERE username = ? AND username = creator";
        try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    String userIDs = rs.getString("userIDs");
                    String[] userIDsArray = userIDs.split(",");
                    ArrayList<String> userIDsList = new ArrayList<>(Arrays.asList(userIDsArray));
                    Task task = new Task(rs.getString("name"), rs.getString("status"), rs.getInt("year"), rs.getInt("month"), rs.getInt("day"), rs.getString("content"), rs.getInt("notificationYear"), rs.getInt("notificationMonth"), rs.getInt("notificationDay"), userIDsList);
                    output_string += (task.toString() + "|");
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        output_string = output_string.substring(0, output_string.length() - 1);
        return output_string;
    }

    /**
     * Finds a user by username in the database.
     * @param username The username of the user to find.
     * @return The User object if found, null otherwise.
     */
    public synchronized static User findByUsername(String username) {
        // Placeholder for actual database logic
        String sql = "SELECT id, username, password FROM UserAccount WHERE username = ?";
        try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            System.out.println("connectsuccess");
            pstmt.setString(1, username);
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    User user = new User(rs.getString("username"), rs.getString("password"));
                    return user;
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Adds a user to the database.
     * @param user The User object to add.
     */
    public synchronized static void addUser(User user) {
        // Placeholder for actual database logic
        if(findByUsername(user.getUsername()) != null) {
            System.out.println("usernmae have been used");
            return;
        }

        String sql  = "INSERT INTO UserAccount(username, password) VALUES(?, ?)";
        try(Connection conn = Connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Updates a user's password in the database.
     * @param username The username of the user.
     * @param newPassword The new password.
     * @return true if the update was successful, false otherwise.
     */
    public synchronized static boolean updatePassword(String username, String newPassword) {
        // Placeholder for actual database logic
        User user = findByUsername(username);
        if (user != null) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }

    /**
     * Updates a user's username in the database.
     * @param oldUsername The current username of the user.
     * @param newUsername The new username.
     * @return true if the update was successful, false otherwise.
     */
    public synchronized static boolean updateUsername(String oldUsername, String newUsername) {
        // Placeholder for actual database logic
        User user = findByUsername(oldUsername);
        if (user != null && findByUsername(newUsername) == null) {
            user.setUsername(newUsername);
            return true;
        }
        return false;
    }

}
