import { useTheme } from '../theme/ThemeContext';
import { useTranslation } from 'react-i18next';



const Footer = () => {  
  const { t } = useTranslation('common');
  const { theme } = useTheme();

  return (
    <footer style={{ backgroundColor: theme.palette.background.footer, padding: '10px 20px', textAlign: 'center' }}>
      <p style={{ color: theme.palette.text.secondary }}>
        {t('footer.text')} &copy; {new Date().getFullYear()}
      </p>
    </footer>
  );
}

export default Footer;