package sample;

import autopatch.Application;
import autopatch.constants.CommonConsts;
import autopatch.domain.EncryptInfo;
import autopatch.utils.FileUtils;
import autopatch.utils.IOUtils;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import sample.consts.GUIconsts;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public AnchorPane anchorPane;
    @FXML
    public JFXButton btn_choseTargetFile;
    @FXML
    public JFXButton btn_choseTargetDir;
    @FXML
    public JFXTextField tf_targetFile;
    @FXML
    public JFXTextField tf_targetDir;
    @FXML
    public JFXTextField tf_targetSecretKey;
    @FXML
    public JFXButton btn_create;
    @FXML
    public JFXDialog dialog_result;
    @FXML
    public JFXButton btn_confirm;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //BD输出目录直接读缓存
        if(!IOUtils.createCache(GUIconsts.targetDirCache)) {
            File cacheFile = new File(GUIconsts.targetDirCache);
            String cache = IOUtils.readCache(cacheFile);
            tf_targetDir.setText(cache);
        }
    }

    /**
     * 选择war包
     */
    @FXML
    public void onChoseTargetFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择待提取的war包文件");
        //先读缓存的目录
        IOUtils.createCache(GUIconsts.srcCache);
        File cacheFile = new File((GUIconsts.srcCache));
        String cache = IOUtils.readCache(cacheFile);
        if(!StringUtils.isBlank(cache)){
            fileChooser.setInitialDirectory(new File(cache));
        }
        //设置扩展名
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xls Files", "*.xls"));
        File file = fileChooser.showOpenDialog(new Stage());
        if(file!=null && file.exists()) {
            //写缓存
            IOUtils.writeCache(cacheFile, file.getParent());
            //设置目标文件值
            String filePath = file.getPath();
            tf_targetFile.setText(filePath);
            EncryptInfo.getInstance().setSrcFile(filePath);
        }
    }

    /**
     * 获取补丁目录文件名
     */
    @FXML
    public void onChoseTargetDirPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择一个目录输出文件");
        //先读缓存的目录
        IOUtils.createCache(GUIconsts.targetDirCache);
        File cacheFile = new File(GUIconsts.targetDirCache);
        String cache = IOUtils.readCache(cacheFile);
        if(!StringUtils.isBlank(cache)){
            directoryChooser.setInitialDirectory(new File(cache));
        }

        File file = directoryChooser.showDialog(new Stage());
        if(file!=null && file.exists()) {
            //写缓存
            IOUtils.writeCache(cacheFile, file.getParent()+ File.separator + file.getName());
            //设置目标文件值
            tf_targetDir.setText(file.getPath());
            EncryptInfo.getInstance().setDest(file.getPath());
        }
    }

    /**
     * 提取补丁事件
     */
    @FXML
    public void onEncrypt() {
        //清空输出字符串缓存
        EncryptInfo.getInstance().resultMessage.delete(0,EncryptInfo.getInstance().resultMessage.length());
        //校验输入，无误时执行提取动作
        if(_checkForm()){
            try {
                //输出补丁文件
                Application.doEncrypt();
            } catch (Exception e) {
                EncryptInfo.getInstance().resultMessage.append(e.getMessage());
                e.printStackTrace();
            }
        }
        _showResultDialog(EncryptInfo.getInstance().resultMessage.toString());
    }

    /**
     * 拖拽war包事件
     * @param dragEvent 拖拽事件对象
     */
    @FXML
    public void DragFile(DragEvent dragEvent) {
        File file = dragEvent.getDragboard().getFiles().get(0);
        String fileName = file.getAbsolutePath();
        if(fileName.endsWith(CommonConsts.XLS)) {
            tf_targetFile.setText(fileName);
            EncryptInfo.getInstance().setSrcFile(fileName);
        }else{
            System.out.println("请拖入一个XLS文件！");
        }
    }

    /**
     * 校验输入参数
     */
    private boolean _checkForm(){
        EncryptInfo encryptInfo = EncryptInfo.getInstance();
        boolean flag = true;
        if(StringUtils.isBlank(encryptInfo.getSrcFile())){
            encryptInfo.resultMessage.append("规范标准加密原文件不能为空！\n");
            flag = false;
        }
        if(StringUtils.isBlank(tf_targetDir.getText())){
            encryptInfo.resultMessage.append("输出目录不能为空！\n");
            flag = false;
        }else{
            encryptInfo.setDest(tf_targetDir.getText());
        }
        if(StringUtils.isBlank(tf_targetSecretKey.getText())){
            encryptInfo.resultMessage.append("票据编码（秘钥）不能为空！\n");
            flag = false;
        }else{
            encryptInfo.setKey(tf_targetSecretKey.getText());
            String fileName = FileUtils.getFileNameNoEx(FileUtils.getRealFileName(encryptInfo.getSrcFile()))+"加密.bs";
            encryptInfo.setFileName(fileName);
        }
        return flag;
    }

    /**
     * 显示结果集
     * @param result 提取补丁的结果
     */
    private void _showResultDialog(String result){
        JFXAlert alert = new JFXAlert((Stage) btn_create.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setPrefWidth(300);
        layout.setPrefHeight(160);
        layout.setHeading(new Label("结果"));
        layout.setBody(new Label(result));
        JFXButton closeButton = new JFXButton("确定");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> alert.hideWithAnimation());
        layout.setActions(closeButton);
        alert.setContent(layout);
        alert.show();
    }
}
