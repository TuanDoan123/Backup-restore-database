package com.tuandoan.controller;

import com.tuandoan.dao.BackupDAO;
import com.tuandoan.model.BackupInformation;
import com.tuandoan.model.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController{
	private final String CREATDEVICESUCCESSFUL = "Tạo device thành công";
	private final String BACKUPSUCCESSFUL = "Sao lưu thành công";
	private final String RESTORESUCCESSFUL = "Phục hồi thành công";
	private final String RESTOREFAILURE = "Phục hồi thất bại vì không tìm thấy bảng full backup phù hợp với thời điểm đã chọn";
	private BackupDAO backupDAO;

	@Autowired
	public HomeController(BackupDAO backupDAO){
		this.backupDAO = backupDAO;
	}

	@RequestMapping("/")
	public String home(@RequestParam(required = false) String databaseName,
					   @RequestParam(required = false) boolean showTime,
					   @RequestParam(required = false) String message,
					   Model model) throws SQLException {
		if(backupDAO.getConnect().getUserName() != null && backupDAO.getConnect().getPassword() != null){
			List<Database> databases = backupDAO.getDatabaseNames();
			if(!databases.isEmpty()){
				model.addAttribute("databases", databases);
				if(databaseName == null){
					databaseName = databases.get(0).getName();
					model.addAttribute("firstDatabaseName", databaseName);
				}else {
					model.addAttribute("dbName", databaseName);
				}
				model.addAttribute("databaseName", databaseName);
			}
			boolean hasDevice = backupDAO.hasDevice(databaseName);
			model.addAttribute("hasDevice", hasDevice);
			List<BackupInformation> backupInformations = backupDAO.getBackupInformations(databaseName);
			model.addAttribute("backupInformations", backupInformations);
			if(!backupInformations.isEmpty()){
				model.addAttribute("hasBackup", true);
				model.addAttribute("firstPosition", backupInformations.get(0).getPosition());
				int[] positions = new int[backupInformations.size()];
				for(int i = 0; i < backupInformations.size(); i++){
					positions[i] = backupInformations.get(i).getPosition();
				}
				model.addAttribute("positions", positions);
			}
			if(showTime){
				String now = LocalDateTime.now().toString();
				now = now.substring(0, now.lastIndexOf(":"));
				model.addAttribute("now", now);
			}
			if(message != null){
				switch (message){
					case "CREATDEVICESUCCESSFUL":
						model.addAttribute("message", CREATDEVICESUCCESSFUL);
						break;
					case "BACKUPSUCCESSFUL":
						model.addAttribute("message", BACKUPSUCCESSFUL);
						break;
					case "RESTORESUCCESSFUL":
						model.addAttribute("message", RESTORESUCCESSFUL);
						break;
					case "RESTOREFAILURE":
						model.addAttribute("message", RESTOREFAILURE);
						break;
				}
			}
			return "home";
		}else{
			return "redirect:/login";
		}
	}

	@RequestMapping("/createDevice")
	public String createDevice(@RequestParam String databaseName) throws SQLException {
		if(backupDAO.getConnect().getUserName() != null && backupDAO.getConnect().getPassword() != null){
			backupDAO.createDevice(databaseName);
			return "redirect:/?databaseName=" + databaseName + "&message=CREATDEVICESUCCESSFUL";
		}else{
			return "redirect:/login";
		}
	}

	@RequestMapping("/backup")
	public String backup(@RequestParam String databaseName, @RequestParam boolean deleteAllBeforeBackup) throws SQLException {
		if(backupDAO.getConnect().getUserName() != null && backupDAO.getConnect().getPassword() != null){
			backupDAO.backup(databaseName, deleteAllBeforeBackup);
			return "redirect:/?databaseName=" + databaseName + "&message=BACKUPSUCCESSFUL";
		}else{
			return "redirect:/login";
		}
	}

	@RequestMapping("/restore")
	public String restore(@RequestParam String databaseName,
						  @RequestParam(required = false) Integer position,
						  @RequestParam(required = false) String time) throws SQLException {
		if(backupDAO.getConnect().getUserName() != null && backupDAO.getConnect().getPassword() != null){
			if(time == null){
				System.out.println("->>>>>> restoreTime == null");
				backupDAO.restore(databaseName, position);
			}else {
				LocalDateTime restoreTime = LocalDateTime.parse(time);
				System.out.println("->>>>>> restoreTime != null");
				int foundPosition = backupDAO.findPositionToRestore(databaseName, restoreTime);
				if(foundPosition != -1){
					System.out.println("->>>> foundPosition != -1, " + foundPosition);
					backupDAO.restore(databaseName, Timestamp.valueOf(restoreTime), foundPosition);
				}else{
					System.out.println("Failure");
					return "redirect:/?databaseName=" + databaseName  + "&message=RESTOREFAILURE";
				}
			}
			return "redirect:/?databaseName=" + databaseName  + "&message=RESTORESUCCESSFUL";
		}else{
			return "redirect:/login";
		}
	}
}
