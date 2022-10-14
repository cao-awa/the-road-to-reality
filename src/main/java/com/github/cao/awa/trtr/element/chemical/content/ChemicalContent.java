package com.github.cao.awa.trtr.element.chemical.content;

import com.github.cao.awa.trtr.element.chemical.*;
import net.minecraft.nbt.*;
import org.json.*;

public class ChemicalContent {
    private final ChemicalElement element;
    private int value;
    private double contentPercentage;

    public ChemicalContent(ChemicalElement element, double contentPercentage, int value) {
        this.element = element;
        this.contentPercentage = contentPercentage;
        this.value = value;
    }

    public ChemicalContent(ChemicalElement element, double contentPercentage) {
        this.element = element;
        this.contentPercentage = contentPercentage;
        this.value = 0;
    }

    public ChemicalElement getElement() {
        return element;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public JSONObject serialize() {
        JSONObject json = new JSONObject();
        json.put("value", value);
        json.put("percent", contentPercentage);
        return json;
    }

    public ChemicalContent deserialize(String str) {
        return deserialize(new JSONObject(str));
    }

    public ChemicalContent deserialize(JSONObject json) {
        this.value = json.getInt("value");
        this.contentPercentage = json.getDouble("percent");
        return this;
    }
}
