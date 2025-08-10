// src/components/ContentEditor.js
import React, { useState } from 'react';
import axios from 'axios';

function ContentEditor({ content, onUpdate }) {
  const [isEditing, setIsEditing] = useState(false);
  const [editedContent, setEditedContent] = useState({
    title: content.title || '',
    articleText: content.articleText || '',
    summary: content.summary || ''
  });
  const [activeTab, setActiveTab] = useState('article');
  const [loading, setLoading] = useState(false);

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSave = async () => {
    setLoading(true);
    try {
      const response = await axios.put(`http://localhost:8080/api/content/edit/${content.id}`, editedContent);
      onUpdate(response.data);
      setIsEditing(false);
      alert('Content saved successfully!');
    } catch (error) {
      console.error('Save failed:', error);
      alert('Failed to save content. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async () => {
    setLoading(true);
    try {
      const response = await axios.put(`http://localhost:8080/api/content/approve/${content.id}`);
      onUpdate(response.data);
      alert('Content approved successfully!');
    } catch (error) {
      console.error('Approval failed:', error);
      alert('Failed to approve content. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleRegenerate = async () => {
    if (!window.confirm('Are you sure you want to regenerate this content? This will replace the current content.')) {
      return;
    }
    
    setLoading(true);
    try {
      const response = await axios.post(`http://localhost:8080/api/content/regenerate/${content.id}`, {
        topic: content.topic
      });
      onUpdate(response.data);
      setIsEditing(false);
      alert('Content regenerated successfully!');
    } catch (error) {
      console.error('Regeneration failed:', error);
      alert('Failed to regenerate content. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setEditedContent({
      title: content.title || '',
      articleText: content.articleText || '',
      summary: content.summary || ''
    });
    setIsEditing(false);
  };

  return (
    <div className="content-editor">
      <div className="editor-header">
        <h2 className="content-title">{content.title}</h2>
        <div className="content-status">
          <span className={`status-badge ${content.status?.toLowerCase()}`}>
            {content.status || 'DRAFT'}
          </span>
        </div>
        <div className="editor-actions">
          <button 
            onClick={handleEdit} 
            className="action-button edit-button"
            disabled={isEditing || loading}
          >
            ✏️ Edit
          </button>
          <button 
            onClick={handleApprove} 
            className="action-button approve-button"
            disabled={loading}
          >
            ✅ Approve
          </button>
          <button 
            onClick={handleRegenerate} 
            className="action-button regenerate-button"
            disabled={loading}
          >
            🔄 Regenerate
          </button>
        </div>
      </div>

      <div className="editor-tabs">
        <button 
          className={`tab ${activeTab === 'article' ? 'active' : ''}`}
          onClick={() => setActiveTab('article')}
        >
          📄 Article
        </button>
        <button 
          className={`tab ${activeTab === 'insights' ? 'active' : ''}`}
          onClick={() => setActiveTab('insights')}
        >
          💡 Insights
        </button>
        <button 
          className={`tab ${activeTab === 'diagrams' ? 'active' : ''}`}
          onClick={() => setActiveTab('diagrams')}
        >
          📊 Diagrams
        </button>
        <button 
          className={`tab ${activeTab === 'related' ? 'active' : ''}`}
          onClick={() => setActiveTab('related')}
        >
          🔗 Related
        </button>
      </div>

      <div className="editor-content">
        {loading && (
          <div className="loading-overlay">
            <div className="loading-spinner"></div>
            <p>Processing...</p>
          </div>
        )}

        {activeTab === 'article' && (
          <div className="article-editor">
            {isEditing ? (
              <div className="edit-form">
                <div className="form-group">
                  <label>Title:</label>
                  <input
                    type="text"
                    value={editedContent.title}
                    onChange={(e) => setEditedContent({...editedContent, title: e.target.value})}
                    className="edit-input"
                    placeholder="Enter article title..."
                  />
                </div>
                <div className="form-group">
                  <label>Summary:</label>
                  <textarea
                    value={editedContent.summary}
                    onChange={(e) => setEditedContent({...editedContent, summary: e.target.value})}
                    className="edit-textarea"
                    rows="3"
                    placeholder="Enter article summary..."
                  />
                </div>
                <div className="form-group">
                  <label>Article Text:</label>
                  <textarea
                    value={editedContent.articleText}
                    onChange={(e) => setEditedContent({...editedContent, articleText: e.target.value})}
                    className="edit-textarea article-textarea"
                    rows="15"
                    placeholder="Enter article content..."
                  />
                </div>
                <div className="edit-actions">
                  <button onClick={handleSave} className="save-button" disabled={loading}>
                    💾 Save Changes
                  </button>
                  <button onClick={handleCancel} className="cancel-button" disabled={loading}>
                    ❌ Cancel
                  </button>
                </div>
              </div>
            ) : (
              <div className="article-display">
                <div className="summary-display">
                  <h3>📝 Summary</h3>
                  <p>{content.summary || 'No summary available'}</p>
                </div>
                <div className="article-text-display">
                  <h3>📄 Full Article</h3>
                  <div className="article-content">
                    {content.articleText || 'No article content available'}
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {activeTab === 'insights' && (
          <div className="insights-tab">
            <h3>💡 Key Insights</h3>
            {content.keyInsights && content.keyInsights.length > 0 ? (
              <ul className="insights-list">
                {content.keyInsights.map((insight, index) => (
                  <li key={index} className="insight-item">{insight}</li>
                ))}
              </ul>
            ) : (
              <p className="no-content">No insights available</p>
            )}
          </div>
        )}

        {activeTab === 'diagrams' && (
          <div className="diagrams-tab">
            <h3>📊 Diagram Ideas</h3>
            {content.diagramIdeas && content.diagramIdeas.length > 0 ? (
              <div className="diagram-ideas">
                {content.diagramIdeas.map((idea, index) => (
                  <div key={index} className="diagram-idea">
                    <span className="diagram-number">{index + 1}</span>
                    <span className="diagram-text">{idea}</span>
                  </div>
                ))}
              </div>
            ) : (
              <p className="no-content">No diagram ideas available</p>
            )}
          </div>
        )}

        {activeTab === 'related' && (
          <div className="related-tab">
            <h3>🔗 Related Topics</h3>
            {content.relatedTopics && content.relatedTopics.length > 0 ? (
              <div className="related-topics">
                {content.relatedTopics.map((topic, index) => (
                  <div key={index} className="related-topic">
                    <span className="topic-bullet">•</span>
                    <span className="topic-text">{topic}</span>
                  </div>
                ))}
              </div>
            ) : (
              <p className="no-content">No related topics available</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default ContentEditor;