package fi.dy.masa.malilib.config.serialization;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.tuple.Pair;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.option.BaseGenericConfig;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig;
import fi.dy.masa.malilib.config.option.BooleanAndDoubleConfig.BooleanAndDouble;
import fi.dy.masa.malilib.config.option.BooleanAndFileConfig;
import fi.dy.masa.malilib.config.option.BooleanAndFileConfig.BooleanAndFile;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig;
import fi.dy.masa.malilib.config.option.BooleanAndIntConfig.BooleanAndInt;
import fi.dy.masa.malilib.config.option.DualColorConfig;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.option.Vec2iConfig;
import fi.dy.masa.malilib.config.option.list.BlackWhiteListConfig;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import fi.dy.masa.malilib.config.value.BlackWhiteList;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import fi.dy.masa.malilib.util.data.json.DataJsonDeserializers;
import fi.dy.masa.malilib.util.data.json.DataJsonSerializers;
import fi.dy.masa.malilib.util.position.Vec2i;

public class JsonConfigSerializers
{
    public static JsonElement saveHotkeyedBooleanConfig(HotkeyedBooleanConfig config)
    {
        JsonObject obj = new JsonObject();
        obj.add("enabled", new JsonPrimitive(config.getBooleanValue()));
        obj.add("hotkey", config.getKeyBind().getAsJsonElement());
        return obj;
    }

    public static JsonElement saveDualColorConfig(DualColorConfig config)
    {
        return DataJsonSerializers.serializeDualColorValue(config.getValue());
    }

    public static JsonElement saveVec2iConfig(Vec2iConfig config)
    {
        return DataJsonSerializers.serializeVec2iValue(config.getValue());
    }

    public static JsonElement saveBooleanAndIntConfig(BooleanAndIntConfig config)
    {
        return DataJsonSerializers.serializeBooleanAndIntValue(config.getValue());
    }

    public static JsonElement saveBooleanAndDoubleConfig(BooleanAndDoubleConfig config)
    {
        return DataJsonSerializers.serializeBooleanAndDoubleValue(config.getValue());
    }

    public static JsonElement saveBooleanAndFileConfig(BooleanAndFileConfig config)
    {
        return DataJsonSerializers.serializeBooleanAndFileValue(config.getValue());
    }

    public static <T extends OptionListConfigValue> JsonElement saveOptionListConfig(OptionListConfig<T> config)
    {
        return DataJsonSerializers.serializeOptionListValue(config.getValue());
    }

    public static <T> JsonElement saveValueListConfig(ValueListConfig<T> config)
    {
        return DataJsonSerializers.serializeValueListAsString(config.getValue(), config.getToStringConverter());
    }

    public static <T> JsonElement saveBlackWhiteListConfig(BlackWhiteListConfig<T> config)
    {
        return DataJsonSerializers.serializeBlackWhiteList(config.getValue());
    }

    public static <T> void loadPrimitiveConfig(Consumer<T> consumer,
                                               Supplier<T> supplier,
                                               JsonElement element,
                                               String configName)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                consumer.accept(supplier.get());
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}' - not a JSON primitive", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }
    }

    public static void loadHotkeyedBooleanConfig(HotkeyedBooleanConfig config, JsonElement element, String configName)
    {
        try
        {
            if (element.isJsonObject())
            {
                JsonObject obj = element.getAsJsonObject();
                boolean booleanValue = JsonUtils.getBooleanOrDefault(obj, "enabled", false);

                if (JsonUtils.hasObject(obj, "hotkey"))
                {
                    config.getKeyBind().setValueFromJsonElement(JsonUtils.getNestedObject(obj, "hotkey", false), configName);
                }

                config.loadHotkeyedBooleanValueFromConfig(booleanValue);
                return;
            }
            else
            {
                MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element);
            }
        }
        catch (Exception e)
        {
            MaLiLib.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", configName, element, e);
        }

        config.loadValue(config.getDefaultValue());
    }

    public static void loadDualColorConfig(DualColorConfig config, JsonElement element, String configName)
    {
        Optional<Pair<Color4f, Color4f>> optional = DataJsonDeserializers.readDualColorValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadBooleanAndFileConfig(BooleanAndFileConfig config, JsonElement element, String configName)
    {
        Optional<BooleanAndFile> optional = DataJsonDeserializers.readBooleanAndFileValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static <T extends OptionListConfigValue> void loadOptionListConfig(OptionListConfig<T> config, JsonElement element, String configName)
    {
        Optional<T> optional = DataJsonDeserializers.readOptionListValue(element, config.getAllValues());
        loadConfigValue(config, optional, element, configName);
    }

    public static <T> void loadValueListConfig(ValueListConfig<T> config, JsonElement element, String configName)
    {
        Optional<ImmutableList<T>> optional = DataJsonDeserializers.readValueList(element, config.getFromStringConverter());
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadVec2iConfig(Vec2iConfig config, JsonElement element, String configName)
    {
        Optional<Vec2i> optional = DataJsonDeserializers.readVec2iValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadBooleanAndIntConfig(BooleanAndIntConfig config, JsonElement element, String configName)
    {
        Optional<BooleanAndInt> optional = DataJsonDeserializers.readBooleanAndIntValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static void loadBooleanAndDoubleConfig(BooleanAndDoubleConfig config, JsonElement element, String configName)
    {
        Optional<BooleanAndDouble> optional = DataJsonDeserializers.readBooleanAndDoubleValue(element);
        loadConfigValue(config, optional, element, configName);
    }

    public static <T> void loadBlackWhiteListConfig(BlackWhiteListConfig<T> config, JsonElement element, String configName)
    {
        Optional<BlackWhiteList<T>> optional = DataJsonDeserializers.readBlackWhiteListValue(element, config);
        loadConfigValue(config, optional, element, configName);
    }

    public static <T, C extends BaseGenericConfig<T>> void loadConfigValue(C config,
                                                                           Optional<T> optional,
                                                                           JsonElement element,
                                                                           String configName)
    {
        if (optional.isPresent())
        {
            config.loadValue(optional.get());
        }
        else
        {
            MaLiLib.LOGGER.warn("Failed to load the config value for '{}' from the JSON element '{}'", configName, element);
            config.loadValue(config.getDefaultValue());
        }
    }
}
