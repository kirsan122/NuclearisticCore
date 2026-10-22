package kirsan122.nuclearisticcore.api;

public interface IConfigurableBE {

    int getDeviceSideMode(int sideId);

    void setDeviceSideMode(int sideId, int mode);

    void refreshCaps();
}


