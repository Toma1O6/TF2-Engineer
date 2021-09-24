package dev.toma.engineermod.common;

/**
 * @author Toma
 * @version 1.0
 */
public interface ILiquidIronStorage {

    /**
     * @return Total iron amount.
     */
    int getFluidVolume();

    /**
     * Sets iron amount
     * @param volume The amount to set
     */
    void setIronVolume(int volume);

    /**
     * Inserts iron into this storage.
     * @param amount The amount to insert
     * @return Amount which couldn't be inserted
     */
    int insertIron(int amount);

    /**
     * Tries to extract specified amount of iron.
     * @param amount The amount to extract
     * @return Actual amount of extracted iron
     */
    int extractIron(int amount);
}
