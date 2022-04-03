package com.tuandoan.dao;

import com.tuandoan.jdbc.Connect;
import com.tuandoan.model.BackupInformation;
import com.tuandoan.model.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
public class BackupDAO {
    private Connect connect;

    @Autowired
    public BackupDAO(Connect connect){
        this.connect = connect;
    }

    public Connect getConnect() {
        return connect;
    }

    public void createDevice(String databaseName) throws SQLException {
        Connection connection = connect.getConnection();
        if(connection != null){
            String deviceName = "DEVICE_" + databaseName;
            String path = "D:\\Devices\\" + deviceName + ".bak";
            String sql = "EXEC sp_addumpdevice 'disk', ?, ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, deviceName);
            preparedStatement.setString(2, path);
            preparedStatement.execute();
        }else{
            System.out.println("Connect failed");
        }
    }

    public void backup(String databaseName, boolean deleteAll) throws SQLException {
        Connection connection = connect.getConnection();
        if(connection != null){
            String deviceName = "DEVICE_" + databaseName;
            String withInit = "";
            if(deleteAll) withInit = "WITH INIT";
            String sql = "BACKUP DATABASE ? TO ? " + withInit;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, deviceName);
            preparedStatement.execute();
        }else{
            System.out.println("Connect failed");
        }
    }

    public void restore(String databaseName, int position) throws SQLException {
        Connection connection = connect.getConnection();
        if(connection != null){
            String stm1 = "ALTER DATABASE " + databaseName + " SET SINGLE_USER WITH ROLLBACK IMMEDIATE";
            Statement statement = connection.createStatement();
            statement.execute(stm1);

            String stm2 = "USE tempdb";
            statement.execute(stm2);

            String stm3 = "RESTORE DATABASE ? FROM  ?  WITH FILE= ?, REPLACE";
            String deviceName = "DEVICE_" + databaseName;
            PreparedStatement preparedStatement = connection.prepareStatement(stm3);
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, deviceName);
            preparedStatement.setInt(3, position);
            preparedStatement.execute();

            String stm4 = "ALTER DATABASE " + databaseName + " SET MULTI_USER";
            statement.execute(stm4);
        }else{
            System.out.println("Connect failed");
        }
    }

    public void restore(String databaseName, Timestamp restoreTime, int position) throws SQLException {
        Connection connection = connect.getConnection();
        if(connection != null){
            String stm1 = "ALTER DATABASE " + databaseName + " SET SINGLE_USER WITH ROLLBACK IMMEDIATE";
            Statement statement = connection.createStatement();
            statement.execute(stm1);

            String stm2 = "BACKUP LOG ? TO DISK = ? WITH INIT";
            String path = "D:\\Devices\\" + databaseName + ".trn";
            PreparedStatement preparedStatement = connection.prepareStatement(stm2);
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, path);
            preparedStatement.execute();

            String stm3 = "USE tempdb";
            statement.execute(stm3);

            String stm4 = "RESTORE DATABASE ? FROM ? WITH FILE = ?, REPLACE, NORECOVERY ";
            String deviceName = "DEVICE_" + databaseName;
            preparedStatement = connection.prepareStatement(stm4);
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, deviceName);
            preparedStatement.setInt(3, position);
            preparedStatement.execute();

            String stm5 = "RESTORE LOG ? FROM DISK = ? WITH STOPAT= ?";
            preparedStatement = connection.prepareStatement(stm5);
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, path);
            preparedStatement.setTimestamp(3, restoreTime);
            preparedStatement.execute();

            String stm6 = "ALTER DATABASE " + databaseName + " SET MULTI_USER";
            statement.execute(stm6);
        }else{
            System.out.println("Connect failed");
        }
    }

    public List<BackupInformation> getBackupInformations(String databaseName) throws SQLException {
        Connection connection = connect.getConnection();
        if(connection != null){
            String stm1 = "SELECT position, name, backup_finish_date , user_name FROM  msdb.dbo.backupset " +
                    "   WHERE     database_name = ? AND type='D' AND " +
                    "     backup_set_id >= " +
                    "        (  SELECT  MAX(backup_set_id) " +
                    "        FROM msdb.dbo.backupset  " +
                    "         WHERE database_name = ? AND type='D'" +
                    "                  AND position=1  " +
                    "        )" +
                    "    ORDER BY position DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(stm1);
            preparedStatement.setString(1, databaseName);
            preparedStatement.setString(2, databaseName);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<BackupInformation> backupInformations = new ArrayList<>();
            while (rs.next()){
                int position = rs.getInt(1);
                String name = rs.getString(2);
                Timestamp backupFinishDate = rs.getTimestamp(3);
                String userName = rs.getString(4);
                BackupInformation backupInformation = new BackupInformation(position, name, backupFinishDate.toLocalDateTime(), userName);
                backupInformations.add(backupInformation);
            }
            return backupInformations;
        }else{
            System.out.println("Connect failed");
            return null;
        }
    }

    public List<Database> getDatabaseNames() throws SQLException {
        Connection connection = connect.getConnection();
        if(connection != null){
            String stm1 = "SELECT        name " +
                    " FROM      sys.databases " +
                    " WHERE    (database_id >= 5) AND (NOT (name LIKE N'ReportServer%')) " +
                    " ORDER BY NAME";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(stm1);
            ArrayList<Database> databases = new ArrayList<>();
            while (rs.next()){
                String name = rs.getString(1);
                databases.add(new Database(name));
            }
            return databases;
        }else{
            System.out.println("Connect failed");
            return null;
        }
    }

    public boolean hasDevice(String databaseName) throws SQLException {
        Connection connection = connect.getConnection();
        if(connection != null){
            String deviceName = "DEVICE_" + databaseName;
            String stm1 = "SELECT * " +
                            " FROM  sys.backup_devices " +
                            " WHERE  name = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(stm1);
            preparedStatement.setString(1, deviceName);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return true;
            }
            return false;
        }else{
            System.out.println("Connect failed");
            return false;
        }
    }

    public int findPositionToRestore(String databaseName, LocalDateTime restoreTime) throws SQLException {
        List<BackupInformation> backupInformations = getBackupInformations(databaseName);
        for(BackupInformation backupInformation : backupInformations){
            if(restoreTime.isAfter(backupInformation.getBackupFinishDate())){
                return backupInformation.getPosition();
            }
        }
        return -1;
    }
}
