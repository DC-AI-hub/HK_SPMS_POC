import {
  getBusinessObject,
  is
} from 'bpmn-js/lib/util/ModelUtil';

import { SelectEntry, isSelectEntryEdited } from '@bpmn-io/properties-panel';

import {
  getExtensionElementsList
} from '../../../utils/ExtensionElementsUtil';

import {
  useService
} from '../../../hooks';

export const EMPTY_OPTION = '';


export function BusinessKeyProps(props) {
  const {
    element
  } = props;

  if (!is(element, 'bpmn:StartEvent') || !hasFormFields(element)) {
    return [];
  }

  return [
    {
      id: 'businessKey',
      component: BusinessKey,
      isEdited: isSelectEntryEdited
    },
  ];
}

function BusinessKey(props) {
  const { element } = props;

  const commandStack = useService('commandStack');
  const translate = useService('translate');

  const formData = getFormData(element);

  const getValue = () => {
    return formData.get('flowable:businessKey') || '';
  };

  const setValue = (value) => {
    commandStack.execute('element.updateModdleProperties', {
      element,
      moddleElement: formData,
      properties: {
        'flowable:businessKey': value
      }
    });
  };

  const getOptions = () => {

    const options = [
      { value: EMPTY_OPTION, label: translate('<none>') }
    ];

    const fields = formData.get('fields');

    fields.forEach((field) => {
      const id = field.get('flowable:id');

      if (id) {
        options.push({
          value: id,
          label: id
        });
      }
    });

    return options;
  };

  return SelectEntry({
    element,
    id: 'businessKey',
    label: translate('Key'),
    getValue,
    setValue,
    getOptions
  });
}


// helper ///////////////////

function getFormData(element) {
  const businessObject = getBusinessObject(element);

  return getExtensionElementsList(businessObject, 'flowable:FormData')[ 0 ];
}

function hasFormFields(element) {
  const businessObject = getBusinessObject(element);

  const formData = getFormData(businessObject);

  return formData && formData.get('flowable:fields').length;
}