# targomo-jackson-datatype-trove
This module facilitates serialization and deserialization of certain Trove objects. So far it supports:
* TIntIntMap
* TIntFloatMap
* TIntObjectMap
* TObjectIntMap

This is a forked project from https://bitbucket.org/marshallpierce/jackson-datatype-trove.
It has been slightly amended to work with our jackson version and we added a few serializers/deserializers. 
In the future we will potentially add more of them.

To include add to maven: 
```
<dependency>
    <groupId>com.targomo</groupId>
    <artifactId>jackson-datatype-trove</artifactId>
    <version>0.0.7</version>
</dependency>
```
To include to your Jackson Object Mapper execute:
```
ObjectMapper om = new ObjectMapper();
//-1 is the "null" value representative for integer values
//-1.0f is the "null" value representative for float values
om.registerModule(new JodaModule()) 
        .registerModule(new TroveModule(-1, -1.0f));  
```

## Change Log:

### Version 0.0.8
* Update jackson to 2.12.6
* Update joda time to 2.10.8
* Update junit to 4.13.1
* Update jacoco to 0.8.4

### Version 0.0.7
* Update jackson to 2.10.1
* Update nexus repository

### Version 0.0.6
* Updated jackson to 2.9.9
* Updated surefire to 2.22.2 and disabled class loader
* gitlab-ci and settings updated

### Version 0.0.5
* added no_entry_value for float types

### Version 0.0.4
* added deserialization/serialization implementations for TIntFloatMap

### Version 0.0.3
* initial import from private repo

