import { useState } from 'react';
import { useDropzone } from 'react-dropzone';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { Button, Dialog, DialogTitle, DialogContent, Box, LinearProgress, Typography } from '@mui/material';
import UploadIcon from '@mui/icons-material/Upload';
import { useTranslation } from 'react-i18next';

interface ImportComponentProps<T> {
  columns: GridColDef[];
  validationRules: ValidationRule<T>[];
  onImport: (data: T[]) => Promise<ImportResult>;
  fileTypes: string[];
  parseFile: (file: File) => Promise<T[]>;
  onComplete?: (result: ImportResult) => void;
}

interface ValidationRule<T> {
  validate: (data: T) => boolean;
  message: string;
}

interface ImportResult {
  successCount: number;
  errorCount: number;
  errors: ImportError[];
}

interface ImportError {
  row: number;
  messages: string[];
}

export function ImportComponent<T>({
  columns,
  validationRules,
  onImport,
  fileTypes,
  parseFile,
  onComplete
}: ImportComponentProps<T>) {
  const { t } = useTranslation();
  const [file, setFile] = useState<File | null>(null);
  const [data, setData] = useState<T[]>([]);
  const [errors, setErrors] = useState<ImportError[]>([]);
  const [isImporting, setIsImporting] = useState(false);
  const [importResult, setImportResult] = useState<ImportResult | null>(null);
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: (acceptedFiles) => {
      if (acceptedFiles[0]) {
        handleFileChange(acceptedFiles[0]);
      }
    }
  });

  const handleFileChange = async (file: File) => {
    setFile(file);
    const parsedData = await parseFile(file);
    setData(parsedData);
    validateData(parsedData);
  };

  const validateData = (data: T[]) => {
    const errors: ImportError[] = [];
    data.forEach((row, index) => {
      const messages: string[] = [];
      validationRules.forEach(rule => {
        if (!rule.validate(row)) {
          messages.push(rule.message);
        }
      });
      if (messages.length > 0) {
        errors.push({ row: index + 1, messages });
      }
    });
    setErrors(errors);
  };

  const handleImport = async () => {
    setIsImporting(true);
    const validData = data.filter((_, index) => 
      !errors.some(error => error.row === index + 1)
    );
    const result = await onImport(validData);
    setImportResult(result);
    setIsImporting(false);
    onComplete?.(result);
  };

  return (
    <Dialog open={true} maxWidth="lg" fullWidth>
      <DialogTitle>{t('common:import')}</DialogTitle>
      <DialogContent>
        <Box sx={{ marginBottom: 16 }}>
          <Box
            {...getRootProps()}
            sx={{
              border: '2px dashed',
              borderColor: isDragActive ? 'success.main' : 'primary.main',
              borderRadius: 1,
              p: 4,
              textAlign: 'center',
              cursor: 'pointer',
              backgroundColor: isDragActive ? 'action.selected' : 'background.paper',
              '&:hover': {
                backgroundColor: 'action.hover',
              }
            }}
          >
            <input {...getInputProps()} accept={fileTypes.join(',')} />
            <Box sx={{ mb: 1 }}>
              <UploadIcon fontSize="large" />
            </Box>
            <Typography variant="h6" gutterBottom>
              {t('import.dragDropTitle')}
            </Typography>
            <Typography variant="body1" sx={{ mb: 2 }}>
              {t('import.dragDropText')}
            </Typography>
            <input
              accept={fileTypes.join(',')}
              style={{ display: 'none' }}
              id="import-file-input"
              type="file"
              onChange={(e) => {
                if (e.target.files && e.target.files[0]) {
                  handleFileChange(e.target.files[0]);
                }
              }}
            />
            <label htmlFor="import-file-input">
              <Button
                variant="contained"
                component="span"
                startIcon={<UploadIcon />}
              >
                {t('common:selectFile')}
              </Button>
            </label>
            {file && (
              <Box sx={{ mt: 2 }}>
                <Typography variant="body1">
                  {t('import.selectedFile')}: {file.name}
                </Typography>
              </Box>
            )}
          </Box>
        </Box>
        
        {data.length > 0 && (
          <div style={{ height: 400, width: '100%' }}>
            <DataGrid
              rows={data.map((row, index) => ({
                ...row,
                id: index,
                status: errors.some(error => error.row === index + 1) ? 'error' : 'valid'
              }))}
              columns={[
                ...columns,
                {
                  field: 'status',
                  headerName: t('common:status'),
                  width: 120,
                  renderCell: (params) => (
                    <span style={{ 
                      color: params.value === 'error' ? 'red' : 'green',
                      fontWeight: 'bold'
                    }}>
                      {t(`common.${params.value}`)}
                    </span>
                  )
                }
              ]}
              pageSizeOptions={[5, 10, 25]}
            />
          </div>
        )}

        {isImporting && <LinearProgress />}

        {importResult && (
          <Box marginTop={2}>
            <p>{t('import.result', {
              success: importResult.successCount,
              error: importResult.errorCount
            })}</p>
          </Box>
        )}

        <Box marginTop={2} display="flex" justifyContent="flex-end" gap={1}>
          <Button onClick={handleImport} disabled={!file || isImporting}>
            {t('common:import')}
          </Button>
          <Button onClick={() => onComplete?.({ successCount: 0, errorCount: 0, errors: [] })}>
            {t('common:cancel')}
          </Button>
        </Box>
      </DialogContent>
    </Dialog>
  );
}
