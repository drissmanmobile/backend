const fs = require('fs');
let settings = {
    "git.ignoreLimitWarning": true,
    "java.configuration.updateBuildConfiguration": "interactive",
    "java.import.exclusions": [
        "**/node_modules/**"
    ]
};
console.log(JSON.stringify(settings, null, 4));
