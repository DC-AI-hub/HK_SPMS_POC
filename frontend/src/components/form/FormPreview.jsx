import { Form } from '@bpmn-io/form-js-viewer';
import { useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Typography } from '@mui/material';

/**
 * Renders a form preview based on provided schema
 * @param {Object} props - Component properties
 * @param {Object} props.schema - Form schema definition
 * @param {Object} [props.data] - Initial form data
 * @param {boolean} [props.readOnly] - Disable form interactions
 */
const FormPreview = ({ schema, data, readOnly }) => {
  const { t } = useTranslation();
  const formContainer = useRef(null);
  const formInstance = useRef(null);

  console.log(schema)
  useEffect(() => {
    if (formContainer.current && schema) {
      // Cleanup previous form instance
      if (formInstance.current) {
        formInstance.current.destroy();
      }
      
      // Initialize new form
      formInstance.current = new Form({
        container: formContainer.current
      });
    }

    return () => {
      if (formInstance.current) {
        formInstance.current.destroy();
      }
    };
  }, []);


  useEffect(()=>{
    
    console.log(schema)
    if(formInstance.current){
        formInstance.current.importSchema(schema, data);
    }
    


  },[schema,data,readOnly])


  if (!schema) {
    return (
      <Box p={3} textAlign="center">
        <Typography variant="body1">
          {t('form.preview.empty')}
        </Typography>
      </Box>
    );
  }

  return (<div ref={formContainer}></div>)
};

export default FormPreview;
