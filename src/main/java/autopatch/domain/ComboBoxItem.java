package autopatch.domain;

/**
 * 下拉框明细实体
 * @author CSH
 * @since 2019/9/5
 */
public class ComboBoxItem {

    /**
     * key值
     */
    private String key;

    /**
     * 展示值
     */
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ComboBoxItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ComboBoxItem() {
    }
}
