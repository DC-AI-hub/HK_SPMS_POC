import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { useTranslation } from 'react-i18next';
import { 
  Card,
  CardHeader,
  CardContent,
  Typography,
  Stack,
  Button,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  Visibility as ViewIcon,
  Edit as EditIcon,
  ContentCopy as CopyIcon
} from '@mui/icons-material';
import JsonViewer from './JsonViewer';

/**
 * Component to display form metadata/schema
 * @param {Object} props Component props
 * @param {Object} props.schema Form schema to display
 * @param {Function} [props.onEdit] Edit callback handler
 * @returns {JSX.Element} Form metadata display component
 */
const FormMeta = ({ schema, onEdit }) => {
  const { t } = useTranslation();
  const [viewMode, setViewMode] = useState('formatted'); // 'formatted' or 'raw'

  const handleCopy = () => {
    navigator.clipboard.writeText(JSON.stringify(schema, null, 2));
  };

  return (
    <Card sx={{ height: '100%' }}>
      <CardHeader
        title={t('form:metadata.title')}
        action={
          <Stack direction="row" spacing={1}>
            <Tooltip title={t('form:metadata.copy')}>
              <IconButton onClick={handleCopy}>
                <CopyIcon />
              </IconButton>
            </Tooltip>
            {onEdit && (
              <Tooltip title={t('form:metadata.edit')}>
                <IconButton onClick={onEdit}>
                  <EditIcon />
                </IconButton>
              </Tooltip>
            )}
            <Button 
              size="small" 
              startIcon={viewMode === 'formatted' ? <ViewIcon /> : <EditIcon />}
              onClick={() => setViewMode(viewMode === 'formatted' ? 'raw' : 'formatted')}
            >
              {t(`form.metadata.${viewMode}View`)}
            </Button>
          </Stack>
        }
      />
      <CardContent>
        {viewMode === 'formatted' ? (
          <JsonViewer 
            json={schema}
            collapseDepth={1}
          />
        ) : (
          <Typography variant="body2" component="pre" sx={{ 
            whiteSpace: 'pre-wrap',
            wordBreak: 'break-all',
            fontFamily: 'monospace'
          }}>
            {JSON.stringify(schema, null, 2)}
          </Typography>
        )}
      </CardContent>
    </Card>
  );
};

FormMeta.propTypes = {
  schema: PropTypes.object.isRequired,
  onEdit: PropTypes.func
};

export default FormMeta;
