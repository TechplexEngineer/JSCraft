
//https://blockly-demo.appspot.com/static/demos/blockfactory/index.html#twmivv
// Blockly.Blocks['event_block_break'] = {
//   init: function() {
//     this.jsonInit({
//       "type": "math_foo",
//       "message0": "When %1 breaks %2 %3 do %4",
//       "args0": [
//         {
//           "type": "field_input",
//           "name": "PLAYER",
//           "text": "player"
//         },
//         {
//           "type": "field_variable",
//           "name": "BLOCK",
//           "variable": "block"
//         },
//         {
//           "type": "input_dummy"
//         },
//         {
//           "type": "input_statement",
//           "name": "EVENTACTION"
//         }
//       ],
//       "inputsInline": true,
//       "colour": 330,
//       "tooltip": 'Sets this variable to be equal to the input.',
//       "helpUrl": 'https://github.com/google/blockly/wiki/Variables#set'
//     });
//     this.contextMenuMsg_ = 'Create "get %1" block';
//     // this.setEditable(false);
//   },
//   contextMenuType_: 'variables_get',
//   *
//    * Add menu option to create getter/setter block for this setter/getter.
//    * @param {!Array} options List of menu options to add to.
//    * @this Blockly.Block
   
//   customContextMenu: function(options) {
//     var vars = ['player', 'block'];
//     for (var i = 0; i < vars.length; i++) {
//       var option = {enabled: true};
//       var name = vars[i];
//       option.text = this.contextMenuMsg_.replace('%1', name);
//       var xmlField = goog.dom.createDom('field', null, name);
//       xmlField.setAttribute('name', 'VAR');
//       var xmlBlock = goog.dom.createDom('block', null, xmlField);
//       xmlBlock.setAttribute('type', this.contextMenuType_);
//       option.callback = Blockly.ContextMenu.callbackFactory(this, xmlBlock);
//       options.push(option);
//     }
//   },
//   getVars: function() {
//     var vars = ['player', 'block'];
//     for (var i = 0, input; input = this.inputList[i]; i++) {
//       for (var j = 0, field; field = input.fieldRow[j]; j++) {
//         if (field instanceof Blockly.FieldVariable) {
//           vars.push(field.getValue());
//         }
//       }
//     }
//     return vars;
//   }
// };


Blockly.Blocks['event_block_break'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("When \"player\" breaks \"block\"")
        .appendField(new Blockly.FieldComboBox([["one", "ONE"], ["two", "TWO"], ["three", "THREE"]]), "NAME");
    this.appendStatementInput("NAME")
        .setCheck(null)
        .appendField("do");
    this.setInputsInline(false);
    this.setOutput(true, null);
    this.setColour(210);
    this.setTooltip('');
    this.setHelpUrl('http://www.example.com/');
  },
  getVars: function() {
    var vars = ['player', 'block'];
    for (var i = 0, input; input = this.inputList[i]; i++) {
      for (var j = 0, field; field = input.fieldRow[j]; j++) {
        if (field instanceof Blockly.FieldVariable) {
          vars.push(field.getValue());
        }
      }
    }
    return vars;
  }
};

Blockly.JavaScript['event_block_break'] = function(block) {
  var variable_player = Blockly.JavaScript.variableDB_.getName(block.getFieldValue('PLAYER'), Blockly.Variables.NAME_TYPE);
  var variable_block = Blockly.JavaScript.variableDB_.getName(block.getFieldValue('BLOCK'), Blockly.Variables.NAME_TYPE);
  var statements_eventaction = Blockly.JavaScript.statementToCode(block, 'EVENTACTION');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};