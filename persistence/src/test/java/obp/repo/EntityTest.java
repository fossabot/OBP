package obp.repo;

import obp.Application;
import obp.model.*;
import obp.object.Location;
import obp.services.*;
import obp.services.ObpEntityDeclarationFactory;
import obp.services.ObpEntityFactory;
import obp.services.ObpPropertyDeclarationFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.stream.events.EntityDeclaration;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith( SpringJUnit4ClassRunner.class )
@SpringBootTest(classes = Application.class)
public class EntityTest {

    @Autowired
    private ObpEntityDeclarationFactory entityDeclarationFactory;

    @Autowired
    private ObpPropertyDeclarationFactory propertyDeclarationFactory;

    @Autowired
    private ObpEntityFactory entityFactory;

    @Autowired
    private ObpEntityDeclarationRepository edRepo;

    @Autowired
    private ObpPropertyDeclarationRepository pdRepo;

    @Autowired
    private ObpEntityRepository entityRepo;

    @Autowired
    private EntityDeclarationValidator entityDeclarationValidator;

    @Autowired
    private EntityValidator entityValidator;

    private static String JUNIT_DATA_SUFFIX = "-junitdata";
    private static String NAME = "name" + JUNIT_DATA_SUFFIX;
    private static String LOCATION = "location" + JUNIT_DATA_SUFFIX;
    private static String DATE_OF_BIRTH = "dateOfBirth" + JUNIT_DATA_SUFFIX;
    private static String PERSON = "Person" + JUNIT_DATA_SUFFIX;

    private Calendar kingsBday;
    private String stringValToTest;
    private Location locationToTest;


    @Before
    public void runMeBeforeEachTest() {
        this.cleanTestData();

        this.kingsBday = Calendar.getInstance();
        this.kingsBday.clear();
        this.kingsBday.set(Calendar.MONTH, Calendar.JANUARY);
        this.kingsBday.set(Calendar.DAY_OF_MONTH, 8);
        this.kingsBday.set(Calendar.YEAR, 1935);

        this.stringValToTest = "some-hokie-string";

        this.locationToTest = new Location();
        this.locationToTest.setLat(new Float(3.14152926535));
        this.locationToTest.setLon(new Float(2.7818));
    }

    /**
     * This needs to be run before each test to make sure the test have a clear
     * road to run on.  If the test data that it creates is problematic because it's
     * still in the test system after JUnits are complete, then this can be run again
     * after tests are complete.  It should always be run before tests, though.
     */
    public void cleanTestData() {
        // find test entity declarations
        Collection<ObpEntityDeclaration> allEds = this.entityDeclarationFactory.getEntityDeclarations().values();
        Collection<ObpEntityDeclaration> testEds = new ArrayList<>();
        for(ObpEntityDeclaration ed : allEds) {
            if(ed.getName().endsWith(JUNIT_DATA_SUFFIX)) {
                testEds.add(ed);
            }
        }

        // drop test entities
        for(ObpEntityDeclaration ed : testEds) {
            // find all associated test entities
            Collection<ObpEntity> entitiesToDelete
                    = this.entityRepo.findByEntityDeclarationId(ed.getId());
            for(ObpEntity eToDrop : entitiesToDelete) {
                this.entityRepo.delete(eToDrop);
            }
        }

        // drop test entity declarations
        for(ObpEntityDeclaration ed : testEds) {
            if(ed.getName().endsWith(JUNIT_DATA_SUFFIX)) {
                this.edRepo.delete(ed);
            }
        }

        // drop test property declarations
        Collection<ObpPropertyDeclaration> allPds = this.propertyDeclarationFactory.getPropertyDeclarationsByName().values();
        for(ObpPropertyDeclaration pd : allPds) {
            if(pd.getName().endsWith(JUNIT_DATA_SUFFIX)) {
                this.pdRepo.delete(pd);
            }
        }
    }

    public void createNamePropertyDeclaration() {
        this.propertyDeclarationFactory.createPropertyDeclaration(
                NAME,  ObpDataType.FREE_TEXT);
    }

    public void createLocationPropertyDeclaration() {
        this.propertyDeclarationFactory.createPropertyDeclaration(
                LOCATION, ObpDataType.LOCATION);
    }

    public void createDateOfBirthPropertyDeclaration() {
        this.propertyDeclarationFactory.createPropertyDeclaration(
                DATE_OF_BIRTH, ObpDataType.DATE);
    }

    public ObpEntityDeclaration createPersonEntityDeclaration() {
        createNamePropertyDeclaration();
        createLocationPropertyDeclaration();
        createDateOfBirthPropertyDeclaration();

        ObpEntityDeclaration personDeclaration = this.entityDeclarationFactory.getOrCreateEntityDeclaration(PERSON);
        this.propertyDeclarationFactory.getPropertyDeclarationNamed(DATE_OF_BIRTH);

        entityDeclarationFactory.associate(personDeclaration,
                this.propertyDeclarationFactory.getPropertyDeclarationNamed(DATE_OF_BIRTH).getId());
        entityDeclarationFactory.associate(personDeclaration,
                this.propertyDeclarationFactory.getPropertyDeclarationNamed(LOCATION).getId());
        entityDeclarationFactory.associate(personDeclaration,
                this.propertyDeclarationFactory.getPropertyDeclarationNamed(NAME).getId());
        return personDeclaration;
    }

    @Test
    public void persistAndConfirmFullEntity() {
        ObpEntityDeclaration oed = this.createPersonEntityDeclaration();
        Assert.assertTrue(this.entityDeclarationValidator.validate(oed));
        Assert.assertEquals(PERSON, oed.getName());
        Assert.assertNotNull(oed.getId());
        Collection<String> pdIds = oed.getPropertyDeclarationIds();
        Collection<ObpPropertyDeclaration> pds = new ArrayList<>();
        for(String pdId : pdIds) {
            pds.add(this.propertyDeclarationFactory.getPropertyDeclarationById(pdId));
        }
        Assert.assertEquals(3, pds.size());
        Map<String, ObpPropertyDeclaration> entPropsByName = new HashMap<>();
        for(ObpPropertyDeclaration pd : pds) {
            entPropsByName.put(pd.getName(), pd);
        }
        ObpPropertyDeclaration pdDob = entPropsByName.get(DATE_OF_BIRTH);
        Assert.assertNotNull(pdDob);
        Assert.assertEquals(ObpDataType.DATE, pdDob.getType());
        pdDob = entPropsByName.get(LOCATION);
        Assert.assertNotNull(pdDob);
        Assert.assertEquals(ObpDataType.LOCATION, pdDob.getType());
        pdDob = entPropsByName.get(NAME);
        Assert.assertNotNull(pdDob);
        Assert.assertEquals(ObpDataType.FREE_TEXT, pdDob.getType());

        // create the entity to test
        ObpEntity myEntity = this.entityFactory.createEntity(oed);

        this.entityFactory.addProperty(myEntity, NAME, this.stringValToTest);

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String bdayAsString = formatter.format(this.kingsBday.getTime());
        this.entityFactory.addProperty(myEntity, DATE_OF_BIRTH, bdayAsString);


        String latLongString = this.locationToTest.getLat() + ","
                + this.locationToTest.getLon();
        this.entityFactory.addProperty(myEntity, LOCATION, latLongString);

        // save it to the DB
        ObpEntity savedEntity = this.entityFactory.save(myEntity);

        Assert.assertTrue(this.entityValidator.validate(savedEntity));

        // make sure it still looks good
        Assert.assertNotNull(savedEntity);
        Assert.assertTrue(savedEntity.isValid());
        Assert.assertNotNull(savedEntity.getId());
        ObpEntityDeclaration ed = this.entityDeclarationFactory.getEntityDeclarationById(
                savedEntity.getEntityDeclarationId());
        Assert.assertTrue(ed.isValid());
        Assert.assertEquals(PERSON, ed.getName());
        Assert.assertEquals(3, ed.getPropertyDeclarationIds().size());
        Collection<ObpProperty> props = savedEntity.getProperties();
        Assert.assertEquals(3, props.size());

        ObpPropertyDeclaration propertyDeclaration
                = this.propertyDeclarationFactory.getPropertyDeclarationNamed(NAME);
        ObpProperty myProp = savedEntity.getPropertyByPropertyDeclarationId(
                propertyDeclaration.getId());
        Assert.assertNotNull(myProp);
        ObpPropertyValue namePropVal = myProp.getValue();
        ObpStringValue nameDataVal = (ObpStringValue) namePropVal.getValue();
        Assert.assertEquals(this.stringValToTest, nameDataVal.getValue());

        propertyDeclaration = this.propertyDeclarationFactory.getPropertyDeclarationNamed(DATE_OF_BIRTH);
        myProp = savedEntity.getPropertyByPropertyDeclarationId(
                propertyDeclaration.getId());
        Assert.assertNotNull(myProp);
        ObpPropertyValue datePropVal = myProp.getValue();
        ObpDateValue dateDataVal = (ObpDateValue) datePropVal.getValue();
        Assert.assertEquals(this.kingsBday.getTime(), dateDataVal.getValue());

        propertyDeclaration = this.propertyDeclarationFactory.getPropertyDeclarationNamed(LOCATION);
        myProp = savedEntity.getPropertyByPropertyDeclarationId(
                propertyDeclaration.getId());
        Assert.assertNotNull(myProp);
        ObpPropertyValue locationPropVal = myProp.getValue();
        ObpLocationValue locationDataVal = (ObpLocationValue) locationPropVal.getValue();
        Location loc = locationDataVal.getValue();
        Assert.assertEquals(this.locationToTest.getLat(), loc.getLat());
        Assert.assertEquals(this.locationToTest.getLon(), loc.getLon());
    }

//    public void testEmptyEntityCreation() {
//        ObpEntityDeclaration entityDeclaration = entityDeclarationFactory.getOrCreateEntityDeclaration(PERSON);
//        ObpEntity person = entityFactory.createFullEntity(entityDeclaration);
//        person.setValueFromString(NAME, "TEST");
//        person.setValueFromString(DATE_OF_BIRTH, "TEST");
//        person.setValueFromString(LOCATION, "TEST");
//
//        Assert.assertTrue(person.getValueAsString(NAME).equals("TEST"));
//        person.getValueAsString(DATE_OF_BIRTH);
//        person.getValueAsString(LOCATION);
//    }
}
