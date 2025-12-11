const fs = require('fs');
const path = require('path');

const localesDir = path.join(__dirname, 'locales');
const languages = fs.readdirSync(localesDir);
const namespaces = ['common', 'header', 'sidebar', 'organization', 'process', 'form', 'user'];

// Step 1: Verify all translation files have matching keys
function verifyKeyConsistency() {
  const keyStructure = {};
  
  namespaces.forEach(ns => {
    keyStructure[ns] = {};
    
    languages.forEach(lang => {
      const filePath = path.join(localesDir, lang, `${ns}.json`);
      if (!fs.existsSync(filePath)) {
        console.error(`❌ Missing ${ns} namespace for ${lang}`);
        return;
      }
      
      const content = JSON.parse(fs.readFileSync(filePath));
      keyStructure[ns][lang] = Object.keys(content);
    });
    
    // Compare keys across languages for this namespace
    const baseLang = 'en';
    const baseKeys = keyStructure[ns][baseLang] || [];
    
    languages.forEach(lang => {
      if (lang === baseLang) return;
      
      const langKeys = keyStructure[ns][lang] || [];
      const missingKeys = baseKeys.filter(k => !langKeys.includes(k));
      
      if (missingKeys.length > 0) {
        console.warn(`⚠️  Missing keys in ${lang}/${ns}:`);
        missingKeys.forEach(k => console.warn(`  - ${k}`));
      }
    });
  });
}

// Step 2: Find unused translation keys
function findUnusedKeys() {
  // This would require parsing component files - more complex implementation
  console.log('Unused key detection requires component analysis - implement later');
}

console.log('Starting translation validation...');
verifyKeyConsistency();
findUnusedKeys();
console.log('Validation complete!');
