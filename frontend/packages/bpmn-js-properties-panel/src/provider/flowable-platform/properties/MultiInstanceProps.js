import {
  getBusinessObject,
  is
} from 'bpmn-js/lib/util/ModelUtil';

import {
  TextFieldEntry,
  isTextFieldEntryEdited,
  CheckboxEntry,
  isCheckboxEntryEdited
} from '@bpmn-io/properties-panel';

import {
  useService
} from '../../../hooks';

import {
  getExtensionElementsList
} from '../../../utils/ExtensionElementsUtil';

import {
  createElement
} from '../../../utils/ElementUtil';


/**
 * @typedef { import('@bpmn-io/properties-panel').EntryDefinition } Entry
 */

/**
 * @returns {Array<Entry>} entries
 */
export function MultiInstanceProps(props) {
  const {
    element
  } = props;

  const loopCharacteristics = getLoopCharacteristics(element);

  let entries = props.entries || [];

  if (!isMultiInstanceSupported(element)) {
    return entries;
  }

  entries.push(
    {
      id: 'collection',
      component: Collection,
      isEdited: isTextFieldEntryEdited
    },
    {
      id: 'elementVariable',
      component: ElementVariable,
      isEdited: isTextFieldEntryEdited
    },
    {
      id: 'multiInstanceAsynchronousBefore',
      component: MultiInstanceAsynchronousBefore,
      isEdited: isCheckboxEntryEdited
    },
    {
      id: 'multiInstanceAsynchronousAfter',
      component: MultiInstanceAsynchronousAfter,
      isEdited: isCheckboxEntryEdited
    });

  if (isAsync(loopCharacteristics)) {
    entries.push(
      {
        id: 'multiInstanceExclusive',
        component: MultiInstanceExclusive,
        isEdited: checkboxIsEditedInverted
      },
      {
        id: 'multiInstanceRetryTimeCycle',
        component: MultiInstanceRetryTimeCycle,
        isEdited: isTextFieldEntryEdited
      }
    );
  }

  return entries;
}

function Collection(props) {
  const { element } = props;

  const debounce = useService('debounceInput');
  const commandStack = useService('commandStack');
  const translate = useService('translate');

  const loopCharacteristics = getLoopCharacteristics(element);

  const getValue = () => {
    return getCollection(element);
  };

  const setValue = (value) => {
    return commandStack.execute(
      'element.updateModdleProperties',
      {
        element,
        moddleElement: loopCharacteristics,
        properties: {
          'flowable:collection': value
        }
      }
    );
  };

  return TextFieldEntry({
    element,
    id: 'collection',
    label: translate('Collection'),
    getValue,
    setValue,
    debounce
  });
}

function ElementVariable(props) {
  const { element } = props;

  const debounce = useService('debounceInput');
  const commandStack = useService('commandStack');
  const translate = useService('translate');

  const loopCharacteristics = getLoopCharacteristics(element);

  const getValue = () => {
    return getElementVariable(element);
  };

  const setValue = (value) => {
    return commandStack.execute(
      'element.updateModdleProperties',
      {
        element,
        moddleElement: loopCharacteristics,
        properties: {
          'flowable:elementVariable': value
        }
      }
    );
  };

  return TextFieldEntry({
    element,
    id: 'elementVariable',
    label: translate('Element variable'),
    getValue,
    setValue,
    debounce
  });
}

function MultiInstanceAsynchronousBefore(props) {
  const { element } = props;

  const commandStack = useService('commandStack');
  const translate = useService('translate');

  const loopCharacteristics = getLoopCharacteristics(element);

  const getValue = () => {
    return isAsyncBefore(loopCharacteristics);
  };

  const setValue = (value) => {

    // overwrite the legacy `async` property, we will use the more explicit `asyncBefore`
    const properties = {
      'flowable:asyncBefore': value,
      'flowable:async': undefined
    };

    commandStack.execute('element.updateModdleProperties', {
      element,
      moddleElement: loopCharacteristics,
      properties
    });
  };

  return CheckboxEntry({
    element,
    id: 'multiInstanceAsynchronousBefore',
    label: translate('Asynchronous before'),
    getValue,
    setValue
  });
}

function MultiInstanceAsynchronousAfter(props) {
  const { element } = props;

  const commandStack = useService('commandStack');
  const translate = useService('translate');

  const loopCharacteristics = getLoopCharacteristics(element);

  const getValue = () => {
    return isAsyncAfter(loopCharacteristics);
  };

  const setValue = (value) => {
    commandStack.execute('element.updateModdleProperties', {
      element,
      moddleElement: loopCharacteristics,
      properties: {
        'flowable:asyncAfter': value,
      }
    });
  };

  return CheckboxEntry({
    element,
    id: 'multiInstanceAsynchronousAfter',
    label: translate('Asynchronous after'),
    getValue,
    setValue
  });
}

function MultiInstanceExclusive(props) {
  const { element } = props;

  const commandStack = useService('commandStack'),
        translate = useService('translate');

  const loopCharacteristics = getLoopCharacteristics(element);

  const getValue = () => {
    return isExclusive(loopCharacteristics);
  };

  const setValue = (value) => {
    commandStack.execute('element.updateModdleProperties', {
      element,
      moddleElement: loopCharacteristics,
      properties: {
        'flowable:exclusive': value
      }
    });
  };

  return CheckboxEntry({
    element,
    id: 'multiInstanceExclusive',
    label: translate('Exclusive'),
    getValue,
    setValue
  });
}

function MultiInstanceRetryTimeCycle(props) {
  const { element } = props;

  const bpmnFactory = useService('bpmnFactory'),
        commandStack = useService('commandStack'),
        translate = useService('translate'),
        debounce = useService('debounceInput');

  const loopCharacteristics = getLoopCharacteristics(element);

  const getValue = () => {
    const failedJobRetryTimeCycle = getExtensionElementsList(loopCharacteristics,
      'flowable:FailedJobRetryTimeCycle')[0];
    return failedJobRetryTimeCycle && failedJobRetryTimeCycle.body;
  };

  const setValue = (value) => {
    const commands = [];

    let extensionElements = loopCharacteristics.get('extensionElements');

    // (1) ensure extension elements
    if (!extensionElements) {
      extensionElements = createElement(
        'bpmn:ExtensionElements',
        { values: [] },
        loopCharacteristics,
        bpmnFactory
      );

      commands.push({
        cmd: 'element.updateModdleProperties',
        context: {
          element,
          moddleElement: loopCharacteristics,
          properties: { extensionElements }
        }
      });
    }

    // (2) ensure failedJobRetryTimeCycle
    let failedJobRetryTimeCycle = getExtensionElementsList(loopCharacteristics,
      'flowable:FailedJobRetryTimeCycle')[0];

    if (!failedJobRetryTimeCycle) {
      failedJobRetryTimeCycle = createElement(
        'flowable:FailedJobRetryTimeCycle',
        { },
        extensionElements,
        bpmnFactory
      );

      commands.push({
        cmd: 'element.updateModdleProperties',
        context: {
          element: loopCharacteristics,
          moddleElement: extensionElements,
          properties: {
            values: [ ...extensionElements.get('values'), failedJobRetryTimeCycle ]
          }
        }
      });
    }

    // (3) update failedJobRetryTimeCycle value
    commands.push({
      cmd: 'element.updateModdleProperties',
      context: {
        element,
        moddleElement: failedJobRetryTimeCycle,
        properties: { body: value }
      }
    });

    // (4) commit all updates
    commandStack.execute('properties-panel.multi-command-executor', commands);
  };

  return TextFieldEntry({
    element,
    id: 'multiInstanceRetryTimeCycle',
    label: translate('Retry time cycle'),
    getValue,
    setValue,
    debounce
  });
}


// helper ////////////////////////////

// generic ///////////////////////////

/**
 * isMultiInstanceSupported - check whether given element supports flowable specific props
 * for multiInstance (ref. <flowable:Cllectable>).
 *
 * @param {djs.model.Base} element
 * @return {boolean}
 */
function isMultiInstanceSupported(element) {
  const loopCharacteristics = getLoopCharacteristics(element);
  return !!loopCharacteristics && is(loopCharacteristics, 'flowable:Collectable');
}

/**
 * getProperty - get a property value of the loop characteristics.
 *
 * @param {djs.model.Base} element
 * @param {string} propertyName
 *
 * @return {any} the property value
 */
function getProperty(element, propertyName) {
  var loopCharacteristics = getLoopCharacteristics(element);
  return loopCharacteristics && loopCharacteristics.get(propertyName);
}

/**
 * getLoopCharacteristics - get loopCharacteristics of a given element.
 *
 * @param {djs.model.Base} element
 * @return {ModdleElement<bpmn:MultiInstanceLoopCharacteristics> | undefined}
 */
function getLoopCharacteristics(element) {
  const bo = getBusinessObject(element);
  return bo.loopCharacteristics;
}

// collection

/**
 * getCollection - get the 'flowable:collection' attribute value of the loop characteristics.
 *
 * @param {djs.model.Base} element
 *
 * @return {string} the 'flowable:collection' value
 */
function getCollection(element) {
  return getProperty(element, 'flowable:collection');
}

// elementVariable

/**
 * getElementVariable - get the 'flowable:elementVariable' attribute value of the loop characteristics.
 *
 * @param {djs.model.Base} element
 *
 * @return {string} the 'flowable:elementVariable' value
 */
function getElementVariable(element) {
  return getProperty(element, 'flowable:elementVariable');
}

// asyncBefore asyncAfter

/**
 * Returns true if the attribute 'flowable:asyncBefore' is set
 * to true.
 *
 * @param  {ModdleElement} bo
 *
 * @return {boolean} a boolean value
 */
function isAsyncBefore(bo) {
  return !!(bo.get('flowable:asyncBefore') || bo.get('flowable:async'));
}

/**
 * Returns true if the attribute 'flowable:asyncAfter' is set
 * to true.
 *
 * @param  {ModdleElement} bo
 *
 * @return {boolean} a boolean value
 */
function isAsyncAfter(bo) {
  return !!bo.get('flowable:asyncAfter');
}

/**
 * Returns true if the attribute 'flowable:exclusive' is set
 * to true.
 *
 * @param  {ModdleElement} bo
 *
 * @return {boolean} a boolean value
 */
function isExclusive(bo) {
  return !!bo.get('flowable:exclusive');
}

/**
 * isAsync - returns true if the attribute 'flowable:asyncAfter' or 'flowable:asyncBefore'
 * is set to true.
 *
 * @param  {ModdleElement} bo
 * @return {boolean}
 */
function isAsync(bo) {
  return isAsyncAfter(bo) || isAsyncBefore(bo);
}

// Checkbox

function checkboxIsEditedInverted(node) {
  return node && !node.checked;
}
