import { useCallback } from 'react';
import { ImportComponent } from '../../components/ImportComponent';
import organizationService from '../../api/idm/organizationService';
import { CompanyType } from './constants';
import { useTranslation } from 'react-i18next';
import Papa from 'papaparse';
import * as XLSX from 'xlsx';

interface CompanyImport {
  name: string;
  active: boolean;
  companyType: keyof typeof CompanyType;
  description?: string;
}

export default function CompanyImportDialog({ onClose }: { onClose: () => void }) {
  const { t } = useTranslation();

  const columns = [
    { field: 'name', headerName: t('organization:company.name'), width: 200 },
    { field: 'active', headerName: t('organization:company.active'), width: 120 },
    { field: 'companyType', headerName: t('organization:company.type'), width: 150 },
    { field: 'description', headerName: t('organization:company.description'), width: 300 },
  ];

  const validationRules = [
    {
      validate: (data: CompanyImport) => !!data.name && data.name.trim().length > 0,
      message: t('import.errors.nameRequired')
    },
    {
      validate: (data: CompanyImport) => typeof data.active === 'boolean',
      message: t('import.errors.activeRequired')
    },
    {
      validate: (data: CompanyImport) => 
        Object.values(CompanyType).includes(data.companyType),
      message: t('import.errors.invalidType')
    }
  ];

  const parseCSV = useCallback(async (file: File) => {
    return new Promise<CompanyImport[]>((resolve, reject) => {
      Papa.parse(file, {
        header: true,
        complete: (results) => {
          resolve(results.data as CompanyImport[]);
        },
        error: (error) => {
          reject(error);
        }
      });
    });
  }, []);

  const parseExcel = useCallback(async (file: File) => {
    const reader = new FileReader();
    return new Promise<CompanyImport[]>((resolve, reject) => {
      reader.onload = (e) => {
        const data = new Uint8Array(e.target?.result as ArrayBuffer);
        const workbook = XLSX.read(data, { type: 'array' });
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const json = XLSX.utils.sheet_to_json(sheet);
        resolve(json as CompanyImport[]);
      };
      reader.onerror = (error) => reject(error);
      reader.readAsArrayBuffer(file);
    });
  }, []);

  const handleImport = async (data: CompanyImport[]) => {
    try {
      const results = await Promise.allSettled(
        data.map(company => organizationService.createCompany(company))
      );
      
      const successCount = results.filter(r => r.status === 'fulfilled').length;
      const errorCount = results.length - successCount;
      
      return {
        successCount,
        errorCount,
        errors: results
          .filter((r): r is PromiseRejectedResult => r.status === 'rejected')
          .map((r, index) => ({
            row: index + 1,
            messages: [r.reason.message || t('import.errors.unknownError')]
          }))
      };
    } catch (error) {
      return {
        successCount: 0,
        errorCount: data.length,
        errors: [{
          row: 1,
          messages: [t('import.errors.importFailed')]
        }]
      };
    }
  };

  return (
    <ImportComponent<CompanyImport>
      columns={columns}
      validationRules={validationRules}
      onImport={handleImport}
      fileTypes={['.csv', '.xlsx']}
      parseFile={async (file) => {
        if (file.name.endsWith('.csv')) {
          return parseCSV(file);
        }
        return parseExcel(file);
      }}
      onComplete={onClose}
    />
  );
}
