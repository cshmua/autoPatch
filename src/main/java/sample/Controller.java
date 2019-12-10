package sample;

import autopatch.Application;
import autopatch.constants.CommonConsts;
import autopatch.domain.ComboBoxItem;
import autopatch.domain.PatchUrl;
import autopatch.helper.GitHelper;
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
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import sample.consts.GUIconsts;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public AnchorPane anchorPane;
    @FXML
    public JFXButton btn_choseGitDir;
    @FXML
    public JFXButton btn_choseTargetWar;
    @FXML
    public JFXButton btn_choseTargetDir;
    @FXML
    public JFXTextField tf_gitDir;
    @FXML
    public JFXTextField tf_targetDir;
    @FXML
    public JFXTextField tf_targetWar;
    @FXML
    public JFXTextField tf_targetFile;
    @FXML
    public JFXButton btn_create;
    @FXML
    public JFXComboBox<ComboBoxItem> cb_oldCommit;
    @FXML
    public JFXComboBox<ComboBoxItem> cb_newCommit;
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
     * 选择git目录方法
     */
    @FXML
    public void onChoseGitDirPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择一个git项目目录");
        //先读缓存的目录
        IOUtils.createCache(GUIconsts.gitCache);
        File cacheFile = new File((GUIconsts.gitCache));
        String cache = IOUtils.readCache(cacheFile);
        if(!StringUtils.isBlank(cache)){
            directoryChooser.setInitialDirectory(new File(cache));
        }

        File file = directoryChooser.showDialog(new Stage());
        if(file!=null && file.exists()) {
            //写缓存
            IOUtils.writeCache(cacheFile, file.getParent());
            //设置目标文件值
            tf_gitDir.setText(file.getPath());
            PatchUrl.getInstance().setDirGit(file.getPath());
            try {
                List<ComboBoxItem> items = GitHelper.getRecentCommit();
                _setComboListItem(items, cb_oldCommit);
                _setComboListItem(items, cb_newCommit);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 选择war包
     */
    @FXML
    public void onChoseTargetWar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择待提取的war包文件");
        //先读缓存的目录
        IOUtils.createCache(GUIconsts.warCache);
        File cacheFile = new File((GUIconsts.warCache));
        String cache = IOUtils.readCache(cacheFile);
        if(!StringUtils.isBlank(cache)){
            fileChooser.setInitialDirectory(new File(cache));
        }
        //设置扩展名
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("War or Jar Files", "*.war","*.jar"));
        File file = fileChooser.showOpenDialog(new Stage());
        if(file!=null && file.exists()) {
            //写缓存
            IOUtils.writeCache(cacheFile, file.getParent());
            //设置目标文件值
            String warFilePath = file.getPath();
            tf_targetWar.setText(warFilePath);
            PatchUrl.getInstance().setDestPath(warFilePath);
        }
    }

    /**
     * 获取补丁目录文件名
     */
    @FXML
    public void onChoseTargetDirPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择一个目录输出补丁文件");
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
            PatchUrl.getInstance().setTargetDir(file.getPath());
        }
    }

    /**
     * 提取补丁事件
     */
    @FXML
    public void onCreatePatch() {
        //校验输入，无误时执行提取动作
        if(_checkForm()){
            try {
                //清空输出字符串缓存
                PatchUrl.getInstance().resultMessage.delete(0,PatchUrl.getInstance().resultMessage.length());
                //清空统计情况
                PatchUrl.getInstance().count=0;
                //输出补丁文件
                Application.doGetPatch2();
            } catch (Exception e) {
                PatchUrl.getInstance().resultMessage.append(e.getMessage());
                e.printStackTrace();
            }
        }
        _showResultDialog(PatchUrl.getInstance().resultMessage.toString());
    }

    /**
     * 拖拽war包事件
     * @param dragEvent 拖拽事件对象
     */
    @FXML
    public void DragFile(DragEvent dragEvent) {
        File file = dragEvent.getDragboard().getFiles().get(0);
        String fileName = file.getAbsolutePath();
        if(fileName.endsWith(CommonConsts.WAR) || fileName.endsWith(CommonConsts.JAR)) {
            tf_targetWar.setText(fileName);
        }else{
            System.out.println("请拖入一个war包！");
        }
    }

    /**
     * 设置下拉框明细方法
     * @param items 明细
     * @param comboBox JFXComboBox组件
     */
    private void _setComboListItem(List<ComboBoxItem> items, JFXComboBox<ComboBoxItem> comboBox) {
        comboBox.getItems().clear();
        comboBox.getItems().addAll(items);
        comboBox.setConverter(new StringConverter<ComboBoxItem>() {
            @Override
            public String toString(ComboBoxItem object) {
                return object==null? "" : object.getValue();
            }

            @Override
            public ComboBoxItem fromString(String string) {
                ComboBoxItem tmp = new ComboBoxItem();
                tmp.setValue(string);
                return tmp;
            }

        });
    }

    /**
     * 校验输入参数
     */
    private boolean _checkForm(){
        PatchUrl patchUrl = PatchUrl.getInstance();
        boolean flag = true;
        if(StringUtils.isBlank(patchUrl.getDestPath())){
            patchUrl.resultMessage.append("war包不能为空！\n");
            flag = false;
        }
        if(StringUtils.isBlank(patchUrl.getDirGit())){
            patchUrl.resultMessage.append("git项目地址不能为空！\n");
            flag = false;
        }
        if(StringUtils.isBlank(tf_targetDir.getText())){
            patchUrl.resultMessage.append("补丁输出目录不能为空！\n");
            flag = false;
        }else{
            patchUrl.setTargetDir(tf_targetDir.getText());
        }
        if(StringUtils.isBlank(tf_targetFile.getText())){
            patchUrl.resultMessage.append("补丁文件名不能为空！\n");
            flag = false;
        }else{
            String fileName = DateFormatUtils.format(new Date(), "yyyyMMdd_HHmm ") + tf_targetFile.getText();
            patchUrl.setTargetFileName(fileName);
        }
        if(cb_oldCommit.getSelectionModel().getSelectedItem()!=null) {
            patchUrl.setOldCommit(cb_oldCommit.getSelectionModel().getSelectedItem().getKey());
        }
        if(cb_newCommit.getSelectionModel().getSelectedItem()==null){
            patchUrl.resultMessage.append("本次提交不能为空！\n");
        }else {
            patchUrl.setNewCommit(cb_newCommit.getSelectionModel().getSelectedItem().getKey());
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
        layout.setPrefWidth(600);
        layout.setPrefHeight(370);
        layout.setHeading(new Label("提取结果"));
        layout.setBody(new Label(result));
        JFXButton closeButton = new JFXButton("确定");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> alert.hideWithAnimation());
        layout.setActions(closeButton);
        alert.setContent(layout);
        alert.show();
    }
}
