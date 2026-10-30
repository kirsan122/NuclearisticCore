package kirsan122.nuclearisticcore.api;

/**
 * Created by kirsan122,
 * Data: 23.10.2026,
 * Time: 20:25
 */
public interface IRedstoneConfigurable {

    RedstoneModes getRedstoneMode();

    void setRedstoneMode(RedstoneModes mode);

    default boolean isRedstoneConfigurable() {
        return true;
    }
}
