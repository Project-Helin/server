package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.geolatte.geom.Point;
import org.geolatte.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.ApplicationLoader;
import play.api.inject.guice.GuiceApplicationBuilder;
import play.api.inject.guice.GuiceApplicationLoader;
import play.libs.Json;

/**
 * Actually this was Copy&Pasted from:
 * https://www.playframework.com/documentation/2.5.x/JavaJsonActions#advanced-usage
 *
 * We need a custom application loader, so that we can replace the ObjectMapper
 * in the Json class.
 */
public class CustomApplicationLoader extends GuiceApplicationLoader {

    private static final Logger logger = LoggerFactory.getLogger(CustomApplicationLoader.class);

    @Override
    public GuiceApplicationBuilder builder(ApplicationLoader.Context context) {
        logger.info("Building ObjectMapperApplicationLoader ");

        ObjectMapper currentMapper = installJsonGeometryConverter();
        // replace the mapper default mapper with own mapper
        Json.setObjectMapper(currentMapper);

        addSettingForModelHelper();

        return super.builder(context);
    }

    private void addSettingForModelHelper() {
        /**
         * We use the BeanUtilsBean to copy properties, see ModelHelper.
         * See for a detailed explanation here: http://stackoverflow.com/a/12719908
         */
        boolean throwException = false; // we don't want to get an exception if null need to be copied
        boolean defaultNull = false;
        int defaultArraySize = 0;
        BeanUtilsBean.getInstance().getConvertUtils().register(throwException, defaultNull, defaultArraySize);
    }

    /**
     * We need to install custom mapper for Point and Polygon, because
     * otherwise they are not mapped from and to JSON.
     */
    private ObjectMapper installJsonGeometryConverter() {
        ObjectMapper currentMapper = Json.mapper();

        /**
         * See http://wiki.fasterxml.com/JacksonHowToCustomDeserializers
         * for why need this.
         */
        SimpleModule module = new SimpleModule();
        module.addSerializer(Point.class, new JsonGeometrySerializer<>());
        module.addDeserializer(Point.class, new JsonGeometryDeserializer<>());

        module.addSerializer(Polygon.class, new JsonGeometrySerializer<>());
        module.addDeserializer(Polygon.class, new JsonGeometryDeserializer<>());

        currentMapper.registerModule(module);
        return currentMapper;
    }
}