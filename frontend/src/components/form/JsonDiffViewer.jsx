import React from 'react';
import { Box, Typography } from '@mui/material';
import { DiffEditor } from '@monaco-editor/react';
import { useTranslation } from 'react-i18next';

const JsonDiffViewer = ({ original, modified }) => {
  const { t } = useTranslation();
  
  return (
    <Box sx={{ 
      height: '100%', 
      display: 'flex', 
      flexDirection: 'column',
      border: '1px solid',
      borderColor: 'divider',
      borderRadius: 1,
      overflow: 'hidden'
    }}>
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'space-between',
        bgcolor: 'background.paper',
        p: 1,
        borderBottom: '1px solid',
        borderColor: 'divider'
      }}>
        <Typography variant="subtitle1">
          {t('form:originalVersion')}
        </Typography>
        <Typography variant="subtitle1">
          {t('form:modifiedVersion')}
        </Typography>
      </Box>
      
      <Box sx={{ flexGrow: 1 }}>
        <DiffEditor
          height="100%"
          language="json"
          original={JSON.stringify(original, null, 2) || ''}
          modified={JSON.stringify(modified, null, 2) || ''}
          options={{
            readOnly: true,
            renderSideBySide: true,
            minimap: { enabled: false },
            scrollBeyondLastLine: false,
            automaticLayout: true,
            lineNumbers: 'on',
            folding: true
          }}
        />
      </Box>
    </Box>
  );
};

export default JsonDiffViewer;
