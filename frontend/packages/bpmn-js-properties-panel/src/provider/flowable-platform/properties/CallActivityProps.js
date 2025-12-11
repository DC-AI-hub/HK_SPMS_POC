import {
  getBusinessObject,
  is
} from 'bpmn-js/lib/util/ModelUtil';

import {
  createElement
} from '../../../utils/ElementUtil';

import {
  addExtensionElements,
  getExtensionElementsList,
  removeExtensionElements
} from '../../../utils/ExtensionElementsUtil';

import {
  useService
} from '../../../hooks';

import {
  TextFieldEntry,
  isTextFieldEntryEdited,
  CheckboxEntry,
  isCheckboxEntryEdited,
  SelectEntry,
  isSelectEntryEdited
} from '@bpmn-io/properties-panel';

import { CalledBpmnProps } from './CalledBpmnProps';
import { CalledCmmnProps } from './CalledCmmnProps';
import { DelegateVariableMappingProps } from './DelegateVariableMappingProps';

/**
 * Defines bpmn:CallActivity properties.
 * Cf. https://docs.flowable.org/manual/7.15/reference/bpmn20/subprocesses/call-activity/
 */
export function CallActivityProps(props) {
  const { element } = props;

  if (!is(element, 'bpmn:CallActivity')) {
    return [];
  }

  const entries = [];

  entries.push({
    id: 'calledElementType',
    component: CalledElementType,
    isEdited: isSelectEntryEdited
  });

  const calledElementType = getCalledElementType(element);

  if (calledElementType === 'bpmn') {
    entries.push(
      ...CalledBpmnProps({ element }),
      ...BusinessKeyProps({ element }),
      ...DelegateVariableMappingProps({ element })
    );
  } else if (calledElementType === 'cmmn') {
    entries.push(
      ...CalledCmmnProps({ element }),
      ...BusinessKeyProps({ element })
    );
  } else {
    entries.push(...BusinessKeyProps({ element }));
  }

  return entries;
}

const DEFAULT_PROPS = {
  calledElement: undefined,
  'flowable:calledElementBinding': undefined,
  'flowable:calledElementVersion': undefined,
  'flowable:calledElementTenantId': undefined,
  'flowable:variableMappingClass' : undefined,
  'flowable:variableMappingDelegateExpression' : undefined,
  'flowable:caseRef': undefined,
  'flowable:caseBinding': undefined,
  'flowable:caseVersion': undefined,
  'flowable:caseTenantId': undefined
};

const DEFAULT_BUSINESS_KEY = '#{execution.processBusinessKey}';

function CalledElementType(props) {
  const {
    element
  } = props;

  const commandStack = useService('commandStack');
  const translate = useService('translate');

  const getValue = () => {
    return getCalledElementType(element);
  };

  const setValue = (value) => {
    const properties = { ...DEFAULT_PROPS };

    if (value === 'bpmn') {
      properties['calledElement'] = '';
    } else if (value === 'cmmn') {
      properties['flowable:caseRef'] = '';
    }

    commandStack.execute('element.updateProperties', { element, properties });
  };

  const getOptions = () => ([
    { value: '', label: translate('<none>') },
    { value: 'bpmn', label: translate('BPMN') },
    { value: 'cmmn', label: translate('CMMN') }
  ]);

  return <SelectEntry
    element={ element }
    id="calledElementType"
    label={ translate('Type') }
    getValue={ getValue }
    setValue={ setValue }
    getOptions={ getOptions }
  />;
}

function BusinessKeyProps(props) {
  const { element } = props;

  const entries = [
    {
      id: 'calledElementBusinessKey',
      component: BusinessKey,
      isEdited: isCheckboxEntryEdited
    }
  ];

  if (hasBusinessKey(element)) {
    entries.push({
      id: 'calledElementBusinessKeyExpression',
      component: BusinessKeyExpression,
      isEdited: isTextFieldEntryEdited
    });
  }

  return entries;
}

function BusinessKey(props) {
  const { element } = props;

  const commandStack = useService('commandStack');
  const modeling = useService('modeling');
  const bpmnFactory = useService('bpmnFactory');
  const translate = useService('translate');

  const getValue = () => {
    return hasBusinessKey(element);
  };

  const setValue = value => {
    if (value) {
      addBusinessKey();
    } else {
      removeBusinessKey();
    }
  };

  function addBusinessKey() {
    const businessObject = getBusinessObject(element);
    let extensionElements = businessObject.get('extensionElements');

    // (1) If there are no extension elements, create flowable:In and update element's properties
    if (!extensionElements) {
      extensionElements = createElement('bpmn:ExtensionElements', {}, businessObject, bpmnFactory);
      const businessKeyItem = createBusinessKey(extensionElements);
      extensionElements.set('values', [ businessKeyItem ]);

      modeling.updateProperties(element, { extensionElements });
    } else {

      // (2) Otherwise, add flowable:In to the existing values
      const businessKeyItem = createBusinessKey(extensionElements);

      addExtensionElements(element, businessObject, businessKeyItem, bpmnFactory, commandStack);
    }
  }

  function createBusinessKey(parent) {
    return createElement(
      'flowable:In',
      {
        businessKey: DEFAULT_BUSINESS_KEY
      },
      parent,
      bpmnFactory
    );
  }

  function removeBusinessKey() {
    const businessObject = getBusinessObject(element);
    const flowableInList = getExtensionElementsList(businessObject, 'flowable:In');
    const businessKeyItems = flowableInList.filter(
      flowableIn => flowableIn.get('businessKey') !== undefined
    );

    removeExtensionElements(element, businessObject, businessKeyItems, commandStack);
  }

  return <CheckboxEntry
    element={ element }
    id="calledElementBusinessKey"
    label={ translate('Business key') }
    getValue={ getValue }
    setValue={ setValue }
  />;
}

function BusinessKeyExpression(props) {
  const { element } = props;

  const commandStack = useService('commandStack');
  const translate = useService('translate');
  const debounce = useService('debounceInput');

  const getValue = () => getBusinessKey(element);
  const setValue = value => {
    const flowableIn = findFlowableInWithBusinessKey(element);

    commandStack.execute('element.updateModdleProperties', {
      element,
      moddleElement: flowableIn,
      properties: {
        businessKey: value || ''
      }
    });
  };

  return <TextFieldEntry
    element={ element }
    id="calledElementBusinessKeyExpression"
    label={ translate('Business key expression') }
    getValue={ getValue }
    setValue={ setValue }
    debounce={ debounce }
  />;
}

// helper //////

function getCalledElementType(element) {
  const businessObject = getBusinessObject(element);

  if (businessObject.get('calledElement') !== undefined) {
    return 'bpmn';
  } else if (businessObject.get('flowable:caseRef') !== undefined) {
    return 'cmmn';
  }

  return '';
}

function hasBusinessKey(element) {
  return getBusinessKey(element) !== undefined;
}

function getBusinessKey(element) {
  const flowableIn = findFlowableInWithBusinessKey(element);

  if (flowableIn) {
    return flowableIn.get('businessKey');
  }
}

function findFlowableInWithBusinessKey(element) {
  const businessObject = getBusinessObject(element);
  const flowableInList = getExtensionElementsList(businessObject, 'flowable:In');

  for (const flowableIn of flowableInList) {
    const businessKey = flowableIn.get('businessKey');

    if (businessKey !== undefined) {
      return flowableIn;
    }
  }
}
