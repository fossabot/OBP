var replace = require('replace-in-file');

const propertiesDefault = 'propertiesServiceRoot: null,';
let propertiesServicesRoot = propertiesDefault;
if (process.env.PROP_SERVICES_ROOT) {
  propertiesServicesRoot = "propertiesServiceRoot: '" + process.env.PROP_SERVICES_ROOT + "',";
}
const searchDefault = 'searchServiceRoot: null,';
let searchServicesRoot = searchDefault;
if (process.env.SEARCH_SERVICES_ROOT) {
  searchServicesRoot = "searchServiceRoot: '" + process.env.SEARCH_SERVICES_ROOT + "',";
}

const omsDefault = 'omsServiceRoot: null,';
let omsServicesRoot = omsDefault;
if (process.env.OMS_SERVICES_ROOT) {
  omsServicesRoot = "omsServiceRoot: '" + process.env.OMS_SERVICES_ROOT + "',";
}

const userDefault = 'userServiceRoot: null,';
let userServicesRoot = userDefault;
if (process.env.USER_SERVICES_ROOT) {
  userServicesRoot = "userServiceRoot: '" + process.env.USER_SERVICES_ROOT + "',";
}

const apiDefault = 'apiRoot: null,';
let apiRoot = apiDefault;
if (process.env.API_ROOT) {
  apiRoot = "apiRoot: '" + process.env.API_ROOT + "',";
}

const options = {
    files: 'src/environments/environment.prod.ts',
    from: [userDefault, apiDefault, omsDefault, searchDefault, propertiesDefault],
    to: [userServicesRoot, apiRoot, omsServicesRoot, searchServicesRoot, propertiesServicesRoot]
};

try {
    let changedFiles = replace.sync(options);
}
catch (error) {
    console.error('Error occurred:', error);
    throw error
}
