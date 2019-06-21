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
    <version>0.0.4</version>
</dependency>
```
To include to your Jackson Object Mapper execute:
```
ObjectMapper om = new ObjectMapper();
om.registerModule(new JodaModule()) 
        .registerModule(new TroveModule(-1)); //-1 is the "null" value representative 
```

## Change Log:

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

