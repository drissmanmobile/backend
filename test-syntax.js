const fs = require('fs');
const acorn = require('acorn');
const jsx = require('acorn-jsx');
try {
  const code = fs.readFileSync('drissman-rn/src/screens/admin/screens.js', 'utf8');
  acorn.Parser.extend(jsx()).parse(code, { sourceType: 'module', ecmaVersion: 2020 });
  console.log("No syntax errors");
} catch (e) {
  console.error("Syntax Error:", e);
}
