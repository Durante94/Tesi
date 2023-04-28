import { Layout } from "antd";
// import logo from './logo.svg';
import './App.css';

function App() {
  const { Header, Content, Footer } = Layout;

  return (
    <Layout>
      <Header className="header">Header</Header>
      <Content className="content">
        Content
        <div style={{ height: 8000 }}>RIMEPIMENTO DI COGLIONI</div>
      </Content>
      <Footer className="footer">Footer</Footer>
    </Layout>
  );
}

export default App;
