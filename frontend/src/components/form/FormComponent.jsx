import React, { useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { TextField, Button, Box, Typography } from '@mui/material';
import { Form } from '@bpmn-io/form-js';

//import '@bpmn-io/form-js'

import '@bpmn-io/form-js/dist/assets/form-js-base.css';
import '@bpmn-io/form-js/dist/assets/form-js-editor.css';
import '@bpmn-io/form-js/dist/assets/form-js.css';
import '@bpmn-io/form-js/dist/assets/properties-panel.css';
import '@bpmn-io/form-js/dist/assets/form-js-playground.css';
import '@bpmn-io/form-js/dist/assets/form-js-editor-base.css';
//import '@bpmn-io/form-js/dist/assets/draggle.css';



/**
 * FormComponent - Renders a dynamic form based on formJS configuration
 * @param {Object} props - Component props
 * @param {Object} props.schema - Form schema definition
 * @param {Function} props.onSubmit - Form submission handler
 */
const FormComponent = ({ schema = { fields: [] }, data , onSubmit }) => {
  const formContainer = useRef(null);
  const formRef = useRef(null);

  useEffect(() => {
    if (!formContainer.current) return;

    // Initialize form instance
    const form = new Form({
      container: formContainer.current,
    });

    // Import form schema from props
    if (schema) {
      form.importSchema(schema,data)
        .then(() => {
          // Handle form submission
          form.on('submit', (event) => {
            if (onSubmit) {
              onSubmit(event.data);
            }
          });
        })
        .catch(err => console.error("Form loading failed", err));
    }

    formRef.current = form;

    return () => form.destroy(); // 卸载时销毁实例
  }, [data]);

  return <div ref={formContainer} className='w-full' />;
};

export default FormComponent;
