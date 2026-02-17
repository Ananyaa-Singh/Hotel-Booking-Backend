package com.example.HotelBooking.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration   // Marks this class as a Spring configuration class (used to define Beans)
public class ModelMapperConfig {

    @Bean   // Tells Spring to create and manage this object as a singleton Bean
    public ModelMapper modelMapper(){

        // Create ModelMapper instance (used for Entity <-> DTO conversion)
        ModelMapper modelMapper = new ModelMapper();

        // Configure how ModelMapper should behave
        modelMapper.getConfiguration()

                // Allow mapping directly using fields (not only getters/setters)
                // Useful when DTOs don't have setters
                .setFieldMatchingEnabled(true)

                // Allow access to private fields using reflection
                // Most Java fields are private, so this helps mapping work properly
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)

                // Defines how strictly fields should match
                // STANDARD = balanced (recommended for most projects)
                // STRICT = exact names only
                // LOOSE = very flexible but risky
                .setMatchingStrategy(MatchingStrategies.STANDARD);

        // Return the configured ModelMapper so Spring can store it in container
        return modelMapper;
    }
}
